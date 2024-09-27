package com.quantuityanalytics.quantuityanalytics.ble

import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.os.Handler
import android.util.Log
import com.quantuityanalytics.quantuityanalytics.utils.SharedPreferencesManager
import com.quantuityanalytics.quantuityanalytics.viewmodel.BreakViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import kotlin.jvm.Throws

@SuppressLint("MissingPermission")
class BleDeviceManager(
    private val context: Context,
    private val bluetoothAdapter: BluetoothAdapter,
    private val listOfDevices: ArrayList<String>,
    private val testViewModel: BreakViewModel) {

    companion object {
        const val TAG = "QuantuityAnalytics.BleDeviceManager"

        const val SCANNING_PERIOD: Int = 5000

        const val COMMAND_START: String = "01"
        const val COMMAND_STOP: String = "00"

        const val BREAK_TEST_SERVICE_UUID: String = "dda4d145-fc52-4705-bb93-dd1f295aa522"
        const val BREAK_TEST_READ_CHARACTERISTIC_UUID: String = "61a885a4-41c3-60d0-9a53-6d652a70d29c"
        const val BREAK_TEST_WRITE_CHARACTERISTIC_UUID: String = "02AA6D7D-23B4-4C84-AF76-98A7699F7FE2"
    }

    private val bluetoothGattMap = mutableMapOf<QABleDevice, BluetoothGatt>()

    private var scanning = false
    private val handler = Handler()

    private val bleScanner by lazy {
        bluetoothAdapter.bluetoothLeScanner
    }

    @Throws(IllegalStateException::class)
    fun startScanning() {
        try {
            if (!scanning) {
                handler.postDelayed({
                    Log.d(TAG, "stop scanning post timer")
                    scanning = false
                    testViewModel.setScannerStatus(false)
                    bleScanner.stopScan(leScanCallback)
                }, SCANNING_PERIOD.toLong())

                scanning = true
                testViewModel.setScannerStatus(true)

                val scanSettings = ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                    .build()
                val scanFilters = listOfDevices.map { macAddress ->
                    ScanFilter.Builder()
                        .setDeviceAddress(macAddress)
                        .build()
                }
                bleScanner.startScan(scanFilters, scanSettings, leScanCallback)
            } else {
                Log.d(TAG, "Trying to start scanning while scan is already on it")
            }

        } catch (ex: Exception) {
            Log.d(TAG, "An error occurred when trying to scan")
            Log.d(TAG, "Bluetooth adapter is off and BleScanner is trying to scan: $ex")
            handler.removeCallbacksAndMessages(null)
            throw IllegalStateException()
        }
    }

    private val leScanCallback = object : ScanCallback() {
        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Log.d(TAG, "Error on scan code $errorCode")
        }

        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            result?.device?.let { device ->
                if (!testViewModel.existDevice(QABleDevice(device))) {
                    Log.d(TAG, "Add device ${device.address}")
                    testViewModel.updateDevice(QABleDevice(device, QABleDevice.STATUS_DISCOVERED))
                }
            }
        }
    }

    fun disconnectFromAllDevices() {
        Log.d(TAG, "disconnectFromAllDevices call")
        bluetoothGattMap.forEach { element ->
            val gatt = element.value
            writeToCharacteristic(gatt, hexStringToByteArray(COMMAND_STOP))
        }
        disconnectAllDevices()
    }

    fun disconnectFromDevices(listOfDevices: ArrayList<QABleDevice>) {
        Log.d(TAG, "disconnectFromDevices call")

        for (device in listOfDevices) {
            Log.d(TAG, "Connecting to device ${device.deviceAddress()}")
            val bluetoothGatt = device.bleDevice.connectGatt(context, false, object : BluetoothGattCallback() {
                override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
                    if (gatt != null) {
                        if (newState == BluetoothProfile.STATE_CONNECTED) {
                            gatt.discoverServices()
                        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                            gatt.close()
                            bluetoothGattMap.remove(device)
                        }
                    }

                }

                override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        gatt?.let { writeToCharacteristic(it, hexStringToByteArray(COMMAND_STOP)) }
                    }
                }

                override fun onCharacteristicWrite(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        gatt?.let {
                            Log.d(TAG, "Characteristic written successfully to ${gatt.device.address}")
                            var actualDevice = testViewModel.getDevice(QABleDevice(bleDevice = gatt.device))
                            actualDevice = QABleDevice(gatt.device, QABleDevice.STATUS_DISCOVERED)
                            testViewModel.updateDevice(actualDevice)
                            gatt.close()
                            bluetoothGattMap.remove(device)
                            Log.d(TAG, "Closed and removed")
                        }
                    }
                }
            })
        }
    }

    fun connectTo(listOfDevices: ArrayList<QABleDevice>) {
        for (device in listOfDevices) {
            Log.d(TAG, "Connecting to device ${device.deviceAddress()}")
            val bluetoothGatt = device.bleDevice.connectGatt(context, false, object : BluetoothGattCallback() {
                override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
                    if (gatt != null) {
                        if (newState == BluetoothProfile.STATE_CONNECTED) {
                            gatt.discoverServices()
                        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                            gatt.close()
                            bluetoothGattMap.remove(device)
                        }
                    }

                }

                override fun onCharacteristicRead(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        val value = characteristic?.value
                        Log.d(TAG, "CONNECT A: Device ${gatt?.device?.address} read characteristic: ${value?.decodeToString()}")

                        if (gatt != null && value != null) {
                            saveRecord(gatt.device, value.decodeToString())
                        } else {
                            Log.d(TAG, "BleDeviceManager was not able to save record (ON READ)")
                        }

                    } else {
                        Log.d(TAG, "ERROR at onCharacteristicRead")
                    }
                }

                override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        gatt?.let { writeToCharacteristic(it, hexStringToByteArray(COMMAND_START)) }
                    }
                }

                override fun onCharacteristicWrite(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        gatt?.let {
                            Log.d(TAG, "B) Characteristic written successfully to ${gatt.device.address}")
                            var actualDevice = testViewModel.getDevice(QABleDevice(bleDevice = gatt.device))
                            actualDevice = QABleDevice(gatt.device, QABleDevice.STATUS_CONNECTED)
                            testViewModel.updateDevice(actualDevice)
                            //gatt.requestMtu(517)
                            //readFromCharacteristic(it)
                        }
                    }
                }
            })

            bluetoothGattMap[device] = bluetoothGatt
            Log.d(TAG, "Now bluetoothGattMap has ${bluetoothGattMap.size} elements")
        }
    }

    fun connectToRead(listOfDevices: ArrayList<QABleDevice>) {
        for (device in listOfDevices) {
            Log.d(TAG, "Connecting to device ${device.deviceAddress()} to read. There are ${bluetoothGattMap.size} elements")
            val bluetoothGatt = bluetoothGattMap[device]
            if (bluetoothGatt != null) {
                readFromCharacteristic(bluetoothGatt)
            } else {
                Log.d(TAG, "No Gatt was found")
            }
        }
    }

    fun connectToGetUpdate(listOfDevices: ArrayList<QABleDevice>) {
        for (device in listOfDevices) {
            Log.d(TAG, "Connecting to device ${device.deviceAddress()}")
            val bluetoothGatt = device.bleDevice.connectGatt(context, false, object : BluetoothGattCallback() {
                override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
                    if (gatt != null) {
                        if (newState == BluetoothProfile.STATE_CONNECTED) {
                            gatt.discoverServices()
                        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                            gatt.close()
                            bluetoothGattMap.remove(device)
                        }
                    }

                }

                override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        gatt?.let { writeToCharacteristic(it, hexStringToByteArray(COMMAND_START)) }
                    }
                }

                override fun onCharacteristicWrite(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        gatt?.let {
                            Log.d(TAG, "C) Characteristic written successfully to ${gatt.device.address}")
                            var actualDevice = testViewModel.getDevice(QABleDevice(bleDevice = gatt.device))
                            actualDevice = QABleDevice(gatt.device, QABleDevice.STATUS_CONNECTED)
                            testViewModel.updateDevice(actualDevice)
                            gatt.requestMtu(517)
                        }
                    }
                }

                override fun onCharacteristicRead(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        val value = characteristic?.value
                        Log.d(TAG, "UPDATE: Device ${gatt?.device?.address} read characteristic: ${value?.toString()}")
                    } else {
                        Log.d(TAG, "ERROR at onCharacteristicRead")
                    }
                }

                override fun onCharacteristicChanged(
                    gatt: BluetoothGatt?,
                    characteristic: BluetoothGattCharacteristic?) {
                    characteristic?.value?.let { value ->
                        Log.d(TAG,"Characteristic string value: ${value.decodeToString()}")

                        if (gatt != null) {
                            saveRecord(gatt.device, value.decodeToString())
                        } else {
                            Log.d(TAG, "BleDeviceManager was not able to save the record (ON UPDATE)")
                        }

//                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
//                        val current = LocalDateTime.now().format(formatter)
//
//                        val record = QABleRecord(
//                            current,
//                            "Unique ID",
//                            gatt?.device!!.address,
//                            value.decodeToString(),
//                            1f
//                        )
//
//                        var deviceToUpdate = testViewModel.getDevice(QABleDevice(bleDevice = gatt.device))
//                        if (deviceToUpdate == null) {
//                            Log.d(TAG, "No previous device was found, creating a new one")
//                            deviceToUpdate = QABleDevice(gatt.device, QABleDevice.STATUS_READING, arrayListOf(record))
//                        } else {
//                            Log.d(TAG, "Updating device with ${deviceToUpdate.deviceAddress()} mac address")
//                            deviceToUpdate.listOfRecords.add(record)
//                        }
//
//                        testViewModel.updateDevice(deviceToUpdate)
                    }
                }

                override fun onMtuChanged(gatt: BluetoothGatt, mtu: Int, status: Int) {
                    val characteristic = gatt.services?.find { service ->
                        service.uuid.toString() == BREAK_TEST_SERVICE_UUID
                    }?.characteristics?.find { characteristics ->
                        characteristics.uuid.toString() == BREAK_TEST_READ_CHARACTERISTIC_UUID
                    }

                    if(characteristic == null){
                        Log.d(TAG, "Couldn't find the characteristics")
                        return
                    }
                    enableNotification(gatt, characteristic)
                }
            })

            bluetoothGattMap[device] = bluetoothGatt
        }
    }

    // Write to the characteristic of a device
    private fun writeToCharacteristic(gatt: BluetoothGatt, value: ByteArray) {
        val service = gatt.getService(UUID.fromString(BREAK_TEST_SERVICE_UUID))
        val characteristic = service.getCharacteristic(UUID.fromString(BREAK_TEST_WRITE_CHARACTERISTIC_UUID))

        characteristic?.let {
            it.value = value
            val success = gatt.writeCharacteristic(it)
            if (success) {
                Log.d(TAG, "A) Characteristic written successfully to ${gatt.device.address}")
            } else {
                Log.d(TAG, "Characteristic was not written properly")
            }
        }
    }

    private fun readFromCharacteristic(gatt: BluetoothGatt) {
        val service = gatt.getService(UUID.fromString(BREAK_TEST_SERVICE_UUID))
        val characteristic = service.getCharacteristic(UUID.fromString(BREAK_TEST_READ_CHARACTERISTIC_UUID))

        characteristic?.let {
            val success = gatt.readCharacteristic(it)
            if (success) {
                Log.d(TAG, "READ was successfully")
            } else {
                Log.d(TAG, "ERROR on READ")
            }
        }
    }

    private fun enableNotification(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic){
        Log.d(TAG, "Enable notification for ${characteristic.uuid}")
        val cccdUuid = UUID.fromString(CCCD_DESCRIPTOR_UUID)
        val payload = when {
            characteristic.isIndicatable() -> BluetoothGattDescriptor.ENABLE_INDICATION_VALUE
            characteristic.isNotifiable() -> BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            else -> return
        }

        characteristic.getDescriptor(cccdUuid)?.let { cccdDescriptor ->
            if(!gatt.setCharacteristicNotification(characteristic, true)){
                Log.d(TAG,"set characteristics notification failed")
                return
            }

            gatt.let { gatt ->
                cccdDescriptor.value = payload
                gatt.writeDescriptor(cccdDescriptor)
            } ?: error("Not connected to a BLE device!")
        }
    }

    private fun saveRecord(device: BluetoothDevice, value: String) {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val current = LocalDateTime.now().format(formatter)
        val spm = SharedPreferencesManager(context)


        val record = QABleRecord(
            current,
            spm.getString(SharedPreferencesManager.SP_DEVICE_NAME_KEY),
            device.address,
            value,
            1f
        )

        var deviceToUpdate = testViewModel.getDevice(QABleDevice(bleDevice = device))
        if (deviceToUpdate == null) {
            Log.d(TAG, "No previous device was found, creating a new one")
            deviceToUpdate = QABleDevice(device, QABleDevice.STATUS_READING, arrayListOf(record))
        } else {
            Log.d(TAG, "Updating device with ${deviceToUpdate.deviceAddress()} mac address")
            deviceToUpdate.listOfRecords.add(record)
        }

        testViewModel.updateDevice(deviceToUpdate)
    }

    private fun hexStringToByteArray(hex: String): ByteArray {
        val len = hex.length
        val data = ByteArray(len / 2)
        var i = 0
        while (i < len) {
            data[i / 2] = ((Character.digit(hex[i], 16) shl 4) + Character.digit(hex[i + 1], 16)).toByte()
            i += 2
        }
        return data
    }

    // Disconnect all devices and close GATT connections
    private fun disconnectAllDevices() {
        for ((device, gatt) in bluetoothGattMap) {
            gatt.close()
        }
        bluetoothGattMap.clear()
    }
}