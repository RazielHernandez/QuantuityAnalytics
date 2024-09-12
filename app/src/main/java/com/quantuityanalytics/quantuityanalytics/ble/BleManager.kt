package com.quantuityanalytics.quantuityanalytics.ble

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
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
import android.util.Log
import androidx.core.app.ActivityCompat
import com.quantuityanalytics.quantuityanalytics.model.BreakRecord
import com.quantuityanalytics.quantuityanalytics.viewmodel.BreakViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
    private val gattMap = mutableMapOf<String, BluetoothGatt>()
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private var commandToWrite: String = "00"

    private val bleScanner by lazy {
        bluetoothAdapter.bluetoothLeScanner
    }

    companion object {
        const val TAG: String = "QuantuityAnalytics.TestActivity"
        const val SCANNING_PERIOD: Int = 5000
        const val COMMAND_START: String = "01"
        const val COMMAND_STOP: String = "00"

        const val BREAK_TEST_SERVICE_UUID: String = "dda4d145-fc52-4705-bb93-dd1f295aa522"
        const val BREAK_TEST_READ_CHARACTERISTIC_UUID: String = "61a885a4-41c3-60d0-9a53-6d652a70d29c"
        const val BREAK_TEST_WRITE_CHARACTERISTIC_UUID: String = "02AA6D7D-23B4-4C84-AF76-98A7699F7FE2"
        const val BREAK_TEST_DESCRIPTOR_UUID: String = "00002902-0000-1000-8000-00805f9b34fb"
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
                    .setDeviceAddress("B4:3A:31:EF:52:8B")      // Filter by device address (MAC)
                    //.setDeviceName("Edge Impulse")            // Filter by device name
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

    fun connectToDeviceToRead(listOfDevice: ArrayList<QABleDevice>?) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d(TAG, "No permissions")
            return
        }

        if (listOfDevice != null) {
            for (device in listOfDevice) {
                Log.d(TAG, "Connecting to device ${device.deviceAddress()}")
                device.bleDevice?.connectGatt(context, false, gattCallbackToRead)
            }
        }
    }

    fun connectToDeviceToWrite(listOfDevice: ArrayList<QABleDevice>?, command: String) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d(TAG, "No permissions")
            return
        }

        this.commandToWrite = command
        testViewModel.setListOfDevices(arrayListOf())

        if (listOfDevice != null) {
            for (device in listOfDevice) {
                Log.d(TAG, "Connecting to write device ${device.deviceAddress()}")
                device.bleDevice?.connectGatt(context, false, gattCallbackToWrite)
            }
        }
    }

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
                if (!testViewModel.existDevice(QABleDevice(device, true))) {
                    Log.d(TAG, "Device found: ${device.name} - ${device.address}")
                    testViewModel.addDevice(QABleDevice(device, true))
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
            val characteristic = findCharacteristics(BREAK_TEST_SERVICE_UUID, BREAK_TEST_READ_CHARACTERISTIC_UUID)
            if(characteristic == null){
                Log.d(TAG, "Couldn't find the characteristics")
                return
            }
            enableNotification(characteristic)
        }

        /*
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
        */

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?) {
            characteristic?.value?.let { value ->
                Log.d(TAG,"Characteristic string value: ${value.decodeToString()}")

//                testViewModel.addRecord(
//                    BreakRecord(
//                        timeStamp = "",
//                        truckId = gatt?.device?.name ?: "Unknown",
//                        sensorId = gatt?.device?.address ?: "No device ID",
//                        breakRecord = value.decodeToString(),
//                        value = 0.01f,
//                        status = BreakRecord.STATUS_CONNECTED
//                    ),
//                    true
//
//                )

            }
        }

    }

    private val gattCallbackToWrite = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
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

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                writeCharacteristic(
                    gatt,
                    BREAK_TEST_SERVICE_UUID,
                    BREAK_TEST_WRITE_CHARACTERISTIC_UUID,
                    hexStringToByteArray(commandToWrite))
            }
        }

        override fun onCharacteristicWrite(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "Characteristic written successfully!")
                coroutineScope.launch {
                    testViewModel.addDevice(QABleDevice(gatt.device,
                        isSelected = true,
                        isConnected = true))
                }
            } else {
                Log.d(TAG, "Failed to write characteristic, status: $status")
                coroutineScope.launch {
                    testViewModel.addDevice(QABleDevice(gatt.device,
                        isSelected = true,
                        isConnected = false
                    ))
                }
            }

            disconnectAndCloseGatt(gatt)

        }
    }

//    fun readCharacteristic(gatt: BluetoothGatt, serviceUUID: String, characteristicUUID: String) {
//        val service = gatt.getService(UUID.fromString(serviceUUID))
//        val characteristic = service?.getCharacteristic(UUID.fromString(characteristicUUID))
//
//        if (characteristic != null) {
//            gatt.readCharacteristic(characteristic)
//        } else {
//            Log.d(TAG,"Characteristic was not founded")
//        }
//    }

    fun writeCharacteristic(gatt: BluetoothGatt, serviceUUID: String, characteristicUUID: String, value: ByteArray) {
        val service = gatt.getService(UUID.fromString(serviceUUID))
        val characteristic = service?.getCharacteristic(UUID.fromString(characteristicUUID))

        if (characteristic != null) {
            characteristic.value = value
            characteristic.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT

            val success = gatt.writeCharacteristic(characteristic)
            if (!success) {
                Log.d(TAG, "Failed to initiate characteristic write")
            }
        } else {
            Log.d(TAG, "Characteristic not found")
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
        val characteristic = findCharacteristics(BREAK_TEST_SERVICE_UUID, BREAK_TEST_READ_CHARACTERISTIC_UUID)
        if(characteristic != null){
            disconnectCharacteristic(characteristic)
        }
        bluetoothGatt?.close()
    }

    fun disconnectAndCloseGatt(bluetoothGatt: BluetoothGatt?) {
        bluetoothGatt?.let {
            it.disconnect()
            it.close()
        }
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

    fun hexStringToByteArray(hex: String): ByteArray {
        val len = hex.length
        val data = ByteArray(len / 2)
        var i = 0
        while (i < len) {
            data[i / 2] = ((Character.digit(hex[i], 16) shl 4) + Character.digit(hex[i + 1], 16)).toByte()
            i += 2
        }
        return data
    }

}