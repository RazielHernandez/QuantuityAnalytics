package com.quantuityanalytics.quantuityanalytics.viewmodel

import android.net.MacAddress
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.quantuityanalytics.quantuityanalytics.ble.QABleDevice
import com.quantuityanalytics.quantuityanalytics.model.BreakRecord

class BreakViewModel: ViewModel() {

    companion object {
        const val ERROR_BLUETOOTH_ADAPTER: Int = 101
    }

    private var mutableListOfDevices = MutableLiveData<ArrayList<QABleDevice>>()
    val listOfDevices: LiveData<ArrayList<QABleDevice>> get() = mutableListOfDevices

    private var mutableScannerStatus = MutableLiveData<Boolean>()
    val scannerStatus: LiveData<Boolean> get() = mutableScannerStatus

    private var mutableStartAction = MutableLiveData<Boolean>()
    val startAction: LiveData<Boolean> get() = mutableStartAction

//    private var mutableListOfRecords = MutableLiveData<ArrayList<BreakRecord>>()
//    val listOfRecords: LiveData<ArrayList<BreakRecord>> get() = mutableListOfRecords

//    private var mutableStepArray = MutableLiveData<ArrayList<TestStep>>()
//    val stepNumber: LiveData<ArrayList<TestStep>> get() = mutableStepArray

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

    fun existDevice(device: QABleDevice): Boolean {
        return mutableListOfDevices.value?.contains(device) ?: false
    }

    fun setStartAction(start: Boolean) {
        mutableStartAction.value = start
    }

//    fun setListOfRecords(arrayOfRecords:ArrayList<BreakRecord>) {
//        mutableListOfRecords.postValue(arrayOfRecords)
//    }
//
//    fun addRecord(record: BreakRecord, overwrite: Boolean) {
//        var newArray = mutableListOfRecords.value
//        if (newArray == null) {
//            newArray = arrayListOf(record)
//        } else {
//
//            if (overwrite) {
//
//                var previousPosition: Int = -1
//                for (actual in newArray) {
//                    if (actual.sensorId == record.sensorId) {
//                        previousPosition = newArray.indexOf(actual)
//                    }
//                }
//
//                if (previousPosition >= 0) {
//                    newArray.removeAt(previousPosition)
//                    newArray.add(record)
//                }
//
//            } else {
//                newArray.add(record)
//            }
//
//
//        }
//        setListOfRecords(newArray)
//    }

//    fun setStepArray(steps: ArrayList<TestStep>) {
//        mutableStepArray.value = steps
//    }

//    fun setStepNumber(step: Int) {
//        mutableStepNumber.value = step
//    }
//
//    fun setDeviceConnectedStatus(device: QABleBluetoothDevice) {
//        val position = mutableListOfDevices.value?.indexOf(device)
//        if (position != null && position >= 0) {
//            mutableListOfDevices.value?.set(position, device)
//        }
//    }

//    fun setErrorCode(errorCode: Int) {
//        mutableErrorCode.value = errorCode
//    }


}