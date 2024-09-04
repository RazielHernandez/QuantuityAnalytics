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
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.pm.PackageManager
import android.os.Handler
import android.os.ParcelUuid
import android.util.Log
import androidx.core.app.ActivityCompat
import com.quantuityanalytics.quantuityanalytics.viewmodel.BreakViewModel
import java.util.UUID
import kotlin.jvm.Throws


@SuppressLint("MissingPermission")
class BleManager(
    private val context: Context,
    private val bluetoothAdapter: BluetoothAdapter,
    val testViewModel: BreakViewModel) {

    private var bluetoothGatt: BluetoothGatt? = null
    private var scanning = false
    private val handler = Handler()

    private val bleScanner by lazy {
        bluetoothAdapter.bluetoothLeScanner
    }

    companion object {
        const val TAG: String = "QuantuityAnalytics.TestActivity"
        const val SCANNING_PERIOD: Int = 6000
        const val BREAK_TEST_SERVICE_UUID: String = "dda4d145-fc52-4705-bb93-dd1f295aa522"
        const val BREAK_TEST_CHARACTERISTIC_UUID: String = "61a885a4-41c3-60d0-9a53-6d652a70d29c"
        const val CCCD_DESCRIPTOR_UUID: String = "00002902-0000-1000-8000-00805f9b34fb"
        const val DEVICE_MAC_ADDRESS_MANUFACTURER: String = "00:00:00"
        const val DEVICE_NAME: String = "IMPULSE"
    }

    @Throws(IllegalStateException::class)
    fun startScanning() {
        Log.d(TAG, "Start scanning...")

        try {
            // Stops scanning after a pre-defined scan period.
            if (!scanning) {
                handler.postDelayed({
                    scanning = false
                    testViewModel.setScannerStatus(false)
                    bleScanner.stopScan(leScanCallback)
                }, SCANNING_PERIOD.toLong())
                testViewModel.setListOfDevices(arrayListOf())
                scanning = true

                val scanSettings = ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                    .build()
                val scanFilter = ScanFilter.Builder()
                    //.setDeviceAddress("B4:3A:31:EF:52:8B") // Filter by device address (MAC)
                    //.setDeviceName("Edge Impulse")          // Filter by device name
                    //.setServiceUuid(ParcelUuid.fromString("dda4d145-fc52-4705-bb93-dd1f295aa522")) // Filter by service UUID
                    .build()
                val filters = listOf(scanFilter)
                //bleScanner.startScan(leScanCallback)
                bleScanner.startScan(filters, scanSettings, leScanCallback)
            } else {
                scanning = false
                testViewModel.setScannerStatus(false)
                bleScanner.stopScan(leScanCallback)
            }
        } catch (ex: Exception) {
            Log.d(TAG, "An error occurred when trying to scan")
            Log.d(TAG, "Bluetooth adapter is turned off and BleScanner is trying to scan")
            handler.removeCallbacksAndMessages(null)
            throw IllegalStateException()
        }
    }

    fun connectToDeviceToRead(device: BluetoothDevice) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d(TAG, "No permissions")
            return
        }
        Log.d(TAG, "Connecting to device ${device.address}")
        device.connectGatt(context, false, gattCallbackToRead)
    }

//    fun connectToDeviceToRead(listOfDevice: Array<BluetoothDevice>) {
//        if (ActivityCompat.checkSelfPermission(
//                context,
//                Manifest.permission.BLUETOOTH_CONNECT
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            Log.d(TAG, "No permissions")
//            return
//        }
//        for (device in listOfDevice) {
//            device.connectGatt(context, false, gattCallbackToRead)
//        }
//        Log.d(TAG, "Connecting to device ${device.address}")
//
//    }

//    fun readCharacteristic(characteristic: BluetoothGattCharacteristic) {
//        bluetoothGatt?.readCharacteristic(characteristic) ?: run {
//            Log.w(TAG, "BluetoothGatt not initialized")
//        }
//    }

    private val leScanCallback = object : ScanCallback() {
        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Log.d(TAG, "Error on scan code $errorCode")
        }

        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            result?.device?.let { device ->
                if (!testViewModel.existDevice(QABleBluetoothDevice(device, true))) {
                    Log.d(TAG, "Device found: ${device.name} - ${device.address}")
                    testViewModel.addDevice(QABleBluetoothDevice(device, true))
                }
            }
        }
    }

    private val gattCallbackToRead = object : BluetoothGattCallback() {

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

            val string = value.toString(Charsets.US_ASCII)
            Log.d(TAG, "Characteristic $characteristic changed: $string")
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
                //Log.d(TAG, "Characteristic changed: ${value.joinToString()}")
                Log.d(TAG,"Characteristic string value: ${value.decodeToString()}")
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
                Log.d(TAG,"set characteristics notification failed")
                return
            }
            writeDescription(cccdDescriptor, BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE)
        }
    }

}