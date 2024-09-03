package com.quantuityanalytics.quantuityanalytics.viewmodel

import android.bluetooth.BluetoothDevice
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.quantuityanalytics.quantuityanalytics.ble.QABleBluetoothDevice
import com.quantuityanalytics.quantuityanalytics.model.BreakRecord

class BreakViewModel: ViewModel() {

    companion object {
        const val ERROR_BLUETOOTH_ADAPTER: Int = 101
    }

    private var mutableListOfDevices = MutableLiveData<ArrayList<QABleBluetoothDevice>>()
    val listOfDevices: LiveData<ArrayList<QABleBluetoothDevice>> get() = mutableListOfDevices

    private var mutableScannerStatus = MutableLiveData<Boolean>()
    val scannerStatus: LiveData<Boolean> get() = mutableScannerStatus

    private var mutableErrorCode = MutableLiveData<Int>()
    val errorCode: LiveData<Int> get() = mutableErrorCode

    fun setListOfDevices(devices: ArrayList<QABleBluetoothDevice>) {
        mutableListOfDevices.value = devices
    }

    fun setScannerStatus(status: Boolean) {
        mutableScannerStatus.value = status
    }

    fun addDevice(device: QABleBluetoothDevice) {
        var newArray = mutableListOfDevices.value
        if (newArray  == null) {
            newArray = arrayListOf(device)
        } else {
            newArray.add(device)
        }
        setListOfDevices(newArray)
    }

    fun existDevice(device: QABleBluetoothDevice): Boolean {

        return mutableListOfDevices.value?.contains(device) ?: false
    }

    fun setErrorCode(errorCode: Int) {
        mutableErrorCode.value = errorCode
    }


}