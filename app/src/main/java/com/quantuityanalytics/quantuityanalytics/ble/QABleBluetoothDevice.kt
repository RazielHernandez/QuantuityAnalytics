package com.quantuityanalytics.quantuityanalytics.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.os.Build
import androidx.annotation.RequiresApi

data class QABleBluetoothDevice (
    var bleDevice: BluetoothDevice,
    var isSelected: Boolean,
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is QABleBluetoothDevice) return false

        return this.bleDevice == other.bleDevice
    }


}