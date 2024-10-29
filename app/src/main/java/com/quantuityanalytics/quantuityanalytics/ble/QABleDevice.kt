package com.quantuityanalytics.quantuityanalytics.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.os.Build

data class QABleDevice (
    var bleDevice: BluetoothDevice,
    var status: Int = STATUS_UNREACHABLE,
    private var listOfRecords: ArrayList<QABleRecord> = arrayListOf(),
) {

    companion object {
        const val STATUS_UNREACHABLE = 0
        const val STATUS_ERROR = -1
        const val STATUS_DISCOVERED = 1
        const val STATUS_CONNECTED = 2
        const val STATUS_READING = 3
    }

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

    fun removeLastRecord() {
        if (listOfRecords.isNotEmpty()) {
            listOfRecords.removeAt(listOfRecords.lastIndex)
        }
    }

    fun getAllRecords(): ArrayList<QABleRecord> {
        return listOfRecords
    }

    fun hasRecords(): Boolean {
        return listOfRecords.isNotEmpty()
    }

    fun addRecord(record: QABleRecord) {
        listOfRecords.add(record)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is QABleDevice) return false

        return this.bleDevice == other.bleDevice
    }

    override fun hashCode(): Int {
        val result = bleDevice.hashCode()
        return result
    }


}