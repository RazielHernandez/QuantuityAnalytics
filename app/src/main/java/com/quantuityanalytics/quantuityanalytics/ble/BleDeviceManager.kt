package com.quantuityanalytics.quantuityanalytics.ble

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Handler
import android.util.Log
import androidx.core.app.ActivityCompat
import com.quantuityanalytics.quantuityanalytics.ble.BleManager.Companion
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
                    testViewModel.updateDevice(QABleDevice(device, true, true))
                }

            }
        }
    }

//    fun disconnectListOfDevices(listOfDevices: ArrayList<QABleDevice>) {
//        for (device in listOfDevices) {
//            Log.d(BleManager.TAG, "Disconnecting from device ${device.deviceAddress()}")
//            val bluetoothGatt = device.bleDevice.connectGatt(context, false, object : BluetoothGattCallback() {
//
//            })
//        }
//    }

    fun disconnectFromAllDevices() {
        bluetoothGattMap.forEach { element ->
            val gatt = element.value
            writeToCharacteristic(gatt, hexStringToByteArray(COMMAND_STOP))

        }

        disconnectAllDevices()
    }

    fun connectListOfDevices(listOfDevices: ArrayList<QABleDevice>) {
        for (device in listOfDevices) {
            Log.d(BleManager.TAG, "Connecting to device ${device.deviceAddress()}")
            val bluetoothGatt = device.bleDevice.connectGatt(context, false, object : BluetoothGattCallback() {
                override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        gatt?.discoverServices()  // Discover services after connecting
                    } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                        gatt?.close()
                        bluetoothGattMap.remove(device)  // Remove the device from the map if disconnected
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
                            Log.d(TAG, "Characteristic written successfully to ${gatt.device.address}")
                            gatt.requestMtu(517)
                            //readFromCharacteristic(it)
                        }
                    }
                }

                override fun onCharacteristicRead(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        val value = characteristic?.value
                        Log.d(TAG, "Device ${gatt?.device?.address} read characteristic: ${value?.toString()}")
                    }
                }

                override fun onCharacteristicChanged(
                    gatt: BluetoothGatt?,
                    characteristic: BluetoothGattCharacteristic?) {
                    characteristic?.value?.let { value ->
                        Log.d(TAG,"Characteristic string value: ${value.decodeToString()}")

                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                        val current = LocalDateTime.now().format(formatter)

                        val record = QABleRecord(
                            current,
                            "Unique ID",
                            gatt?.device!!.address,
                            value.decodeToString(),
                            1f
                        )

                        var deviceToUpdate = testViewModel.getDevice(QABleDevice(bleDevice = gatt.device))
                        if (deviceToUpdate == null) {
                            Log.d(TAG, "No previous device was found, creating a new one")
                           deviceToUpdate = QABleDevice(gatt.device, true, true, arrayListOf(record))
                        } else {
                            Log.d(TAG, "Updating device with ${deviceToUpdate.deviceAddress()} mac address")
                            deviceToUpdate.listOfRecords.add(record)
                        }

                        testViewModel.updateDevice(deviceToUpdate)
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
                Log.d(TAG, "Characteristic written successfully to ${gatt.device.address}")
            } else {
                Log.d(TAG, "Characteristic was not written properly")
            }
        }
    }

    private fun readFromCharacteristic(gatt: BluetoothGatt) {
        val service = gatt.getService(UUID.fromString(BREAK_TEST_SERVICE_UUID))
        val characteristic = service.getCharacteristic(UUID.fromString(BREAK_TEST_READ_CHARACTERISTIC_UUID))

        characteristic?.let {
            gatt.readCharacteristic(it)
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

    // Disconnect all devices and close GATT connections
    fun disconnectAllDevices() {
        for ((device, gatt) in bluetoothGattMap) {
            gatt.close()
        }
        bluetoothGattMap.clear()
    }
}