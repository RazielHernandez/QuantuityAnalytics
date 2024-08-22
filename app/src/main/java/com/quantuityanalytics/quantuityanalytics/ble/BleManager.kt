package com.quantuityanalytics.quantuityanalytics.ble

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.pm.PackageManager
import android.os.Handler
import android.util.Log
import androidx.core.app.ActivityCompat
import java.util.UUID


@SuppressLint("MissingPermission")
class BleManager(
    private val context: Context,
    private val bluetoothAdapter: BluetoothAdapter,
    val deviceAdapter: BleDeviceAdapter) {

    private var bluetoothGatt: BluetoothGatt? = null
    private var scanning = false
    private val handler = Handler()

    private val bleScanner by lazy {
        bluetoothAdapter.bluetoothLeScanner
    }

    companion object {
        const val TAG: String = "QuantuityAnalytics.TestActivity"
        const val SCANNING_PERIOD: Int = 8000
        const val BREAK_TEST_SERVICE_UUID: String = "00001809-0000-1000-8000-00805f9b34fb"
        const val BREAK_TEST_CHARACTERISTIC_UUID: String = "00002a1c-0000-1000-8000-00805f9b34fb"
        const val CCCD_DESCRIPTOR_UUID: String = "00002902-0000-1000-8000-00805f9b34fb"
    }

    fun startScanning() {
        Log.d(TAG, "Start scanning...")
        deviceAdapter.cleanDeviceList()

        // Stops scanning after a pre-defined scan period.
        if (!scanning) {
            handler.postDelayed({
                scanning = false
                bleScanner.stopScan(leScanCallback)
            }, SCANNING_PERIOD.toLong())
            scanning = true
            bleScanner.startScan(leScanCallback)
        } else {
            scanning = false
            bleScanner.stopScan(leScanCallback)
        }
    }

    fun connectToDevice(device: BluetoothDevice) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d(TAG, "No permissions")
            return
        }
        Log.d(TAG, "Connecting to device ${device.address}")
        device.connectGatt(context, false, gattCallback)
    }

    fun readCharacteristic(characteristic: BluetoothGattCharacteristic) {
        bluetoothGatt?.readCharacteristic(characteristic) ?: run {
            Log.w(TAG, "BluetoothGatt not initialized")
        }
    }

    private val leScanCallback = object : ScanCallback() {

        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            Log.d(TAG, "leScanCallback")
            super.onScanResult(callbackType, result)
            result?.device?.let { device ->
                Log.d(TAG, "Device found: ${device.name} - ${device.address}")
                if (!deviceAdapter.containDevice(device)) {
                    Log.d(TAG, "Adding a new device")
                    deviceAdapter.addDevice(device)
                    deviceAdapter.notifyDataSetChanged()
                } else {
                    Log.d(TAG, "Device already saved: ${device.address}")
                }
            }
        }
    }

    private val gattCallback = object : BluetoothGattCallback() {

        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            when (newState) {
                BluetoothGatt.STATE_CONNECTED -> {
                    Log.d(TAG, "Connected to GATT server.")
                    this@BleManager.bluetoothGatt = gatt
                    gatt?.discoverServices()
                }
                BluetoothGatt.STATE_DISCONNECTED -> {
                    Log.d(TAG, "Disconnected from GATT server.")
                    gatt?.close()
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                gatt?.services?.forEach { service ->
                    Log.d(TAG, "Service UUID: ${service.uuid}")
                    val characteristics = service.characteristics
                    characteristics.forEach { characteristic ->
                        Log.d(TAG, "Characteristic UUID: ${characteristic.uuid}")
                        //gatt.readCharacteristic(characteristic)
                    }

                    gatt.requestMtu(517)
                }
            } else {
                Log.w(TAG, "onServicesDiscovered received: $status")
            }
        }

        override fun onMtuChanged(gatt: BluetoothGatt, mtu: Int, status: Int) {
            val characteristic = findCharacteristics(BREAK_TEST_SERVICE_UUID, BREAK_TEST_CHARACTERISTIC_UUID)
            if(characteristic == null){
                Log.d(TAG, "Couldn't find the characteristics")
                return
            }
            enableNotification(characteristic)
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                characteristic?.value?.let { value ->
                    Log.d(TAG, "Characteristic read: ${value.joinToString()}")
                }
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray
        ) {
            super.onCharacteristicChanged(gatt, characteristic, value)
            Log.d(TAG, "Characteristic $characteristic changed: $value")

        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "Characteristic written successfully")
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?) {
            characteristic?.value?.let { value ->
                Log.d(TAG, "Characteristic changed: ${value.joinToString()}")
            }
        }


    }

    private fun enableNotification(characteristic: BluetoothGattCharacteristic){
        Log.d(TAG, "Enable notification for ${characteristic.uuid}")
        val cccdUuid = UUID.fromString(CCCD_DESCRIPTOR_UUID)
        val payload = when {
            characteristic.isIndicatable() -> BluetoothGattDescriptor.ENABLE_INDICATION_VALUE
            characteristic.isNotifiable() -> BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            else -> return
        }

        characteristic.getDescriptor(cccdUuid)?.let { cccdDescriptor ->
            if(bluetoothGatt?.setCharacteristicNotification(characteristic, true) == false){
                Log.d(TAG,"set characteristics notification failed")
                return
            }
            writeDescription(cccdDescriptor, payload)
        }
    }

    fun closeConnection() {
        bleScanner.stopScan(leScanCallback)
        val characteristic = findCharacteristics(BREAK_TEST_SERVICE_UUID, BREAK_TEST_CHARACTERISTIC_UUID)
        if(characteristic != null){
            disconnectCharacteristic(characteristic)
        }
        bluetoothGatt?.close()
    }

    private fun findCharacteristics(serviceUUID: String, characteristicsUUID:String):BluetoothGattCharacteristic?{
        return bluetoothGatt?.services?.find { service ->
            service.uuid.toString() == serviceUUID
        }?.characteristics?.find { characteristics ->
            characteristics.uuid.toString() == characteristicsUUID
        }
    }

    private fun writeDescription(descriptor: BluetoothGattDescriptor, payload: ByteArray){
        bluetoothGatt?.let { gatt ->
            descriptor.value = payload
            gatt.writeDescriptor(descriptor)
        } ?: error("Not connected to a BLE device!")
    }

    private fun disconnectCharacteristic(characteristic: BluetoothGattCharacteristic){
        val cccdUuid = UUID.fromString(CCCD_DESCRIPTOR_UUID)
        characteristic.getDescriptor(cccdUuid)?.let { cccdDescriptor ->
            if(bluetoothGatt?.setCharacteristicNotification(characteristic,false) == false){
                Log.d(TAG,"set charateristics notification failed")
                return
            }
            writeDescription(cccdDescriptor, BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE)
        }
    }

}