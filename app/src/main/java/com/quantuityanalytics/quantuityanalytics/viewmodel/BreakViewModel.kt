package com.quantuityanalytics.quantuityanalytics.viewmodel

import android.net.MacAddress
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.quantuityanalytics.quantuityanalytics.ble.QABleDevice
import com.quantuityanalytics.quantuityanalytics.model.BreakRecord

class BreakViewModel: ViewModel() {

    private var mutableListOfDevices = MutableLiveData<ArrayList<QABleDevice>>()
    val listOfDevices: LiveData<ArrayList<QABleDevice>> get() = mutableListOfDevices

    private var mutableScannerStatus = MutableLiveData<Boolean>()
    val scannerStatus: LiveData<Boolean> get() = mutableScannerStatus

    private var mutableStartAction = MutableLiveData<Boolean>()
    val startAction: LiveData<Boolean> get() = mutableStartAction

    private var mutableReadAction = MutableLiveData<Boolean>()
    val readAction: LiveData<Boolean> get() = mutableReadAction

    fun setReadAction(read: Boolean) {
        mutableReadAction.value = read
    }

    fun setListOfDevices(devices: ArrayList<QABleDevice>) {
        mutableListOfDevices.postValue(devices)
    }

    fun setScannerStatus(status: Boolean) {
        mutableScannerStatus.value = status
    }

    fun updateDevice(device: QABleDevice) {
        val newArray = mutableListOfDevices.value ?: arrayListOf()
        if (mutableListOfDevices.value?.contains(device) == true) {
            val index = newArray.indexOf(device)
            newArray.removeAt(index)
            newArray.add(device)
        } else {
            newArray.add(device)
        }
        setListOfDevices(newArray)
    }

    fun addDevice(device: QABleDevice) {
        var newArray = mutableListOfDevices.value
        if (newArray  == null) {
            newArray = arrayListOf(device)
        } else {
            newArray.add(device)
        }
        setListOfDevices(newArray)
    }

    fun getDevice(device: QABleDevice): QABleDevice? {
        if (existDevice(device)) {
            val index = mutableListOfDevices.value?.indexOf(device)
            return mutableListOfDevices.value?.get(index!!)
        }

        return null
    }

    fun removeLastRecordFromAll() {

    }

    fun existDevice(device: QABleDevice): Boolean {
        return mutableListOfDevices.value?.contains(device) ?: false
    }

    fun setStartAction(start: Boolean) {
        mutableStartAction.value = start
    }

}