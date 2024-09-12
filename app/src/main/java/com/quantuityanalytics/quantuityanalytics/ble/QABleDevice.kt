package com.quantuityanalytics.quantuityanalytics.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.os.Build

data class QABleDevice (
    var bleDevice: BluetoothDevice,
    var isSelected: Boolean = false,
    var isConnected: Boolean = false,
    var listOfRecords: ArrayList<QABleRecord> = arrayListOf(),
) {
    @SuppressLint("MissingPermission")
    fun deviceName(): String {
        if (bleDevice.name == null) {
            return "No device name"
        }else {
            return bleDevice.name
        }
    }

    fun deviceAddress(): String {
        if (bleDevice.address == null) {
            return "No MAC address"
        } else {
            return bleDevice.address
        }
    }

    @SuppressLint("MissingPermission")
    fun deviceAlias(): String? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            bleDevice.alias
        } else {
            "default device alias"
        }
    }

    @SuppressLint("MissingPermission")
    fun deviceType(): Int {
        return bleDevice.type
    }

    fun getLastRecord(): QABleRecord {
        if (listOfRecords.isEmpty()) {
            return QABleRecord.getDefaultRecord(bleDevice.address)
        }
        return listOfRecords.last()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is QABleDevice) return false

        return this.bleDevice == other.bleDevice
    }

    override fun hashCode(): Int {
        var result = bleDevice.hashCode()
        result = 31 * result + isSelected.hashCode()
        result = 31 * result + isConnected.hashCode()
        return result
    }


}