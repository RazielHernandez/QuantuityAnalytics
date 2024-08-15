package com.quantuityanalytics.quantuityanalytics.viewmodel

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DeviceViewModel: ViewModel() {
    private val mutableDeviceList = MutableLiveData<List<BluetoothDevice>>()
    val deviceList: LiveData<List<BluetoothDevice>> get() = mutableDeviceList

    private val mutableScanStatus = MutableLiveData<Boolean>()
    val isScanning: LiveData<Boolean> get() = mutableScanStatus

    fun setScanStatus(status: Boolean) {
        mutableScanStatus.value = status
    }

    fun setBluetoothDevice(deviceList: List<BluetoothDevice>) {
        mutableDeviceList.value = deviceList
    }

    fun addBluetoothDevice(device: BluetoothDevice) {
        mutableDeviceList.value = (mutableDeviceList.value ?: emptyList()) + device
    }

}