package com.quantuityanalytics.quantuityanalytics.viewmodel

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.quantuityanalytics.quantuityanalytics.model.BreakRecord

class BreakViewModel: ViewModel() {

    private var mutableListOfDevices = MutableLiveData<ArrayList<BluetoothDevice>>()
    val listOfDevices: LiveData<ArrayList<BluetoothDevice>> get() = mutableListOfDevices

//    private var mutableBreakRecord = MutableLiveData<BreakRecord>()
//    val breakRecord: LiveData<BreakRecord> get() = mutableBreakRecord
//
//    fun setBreakRecord(breakRecord: BreakRecord) {
//        mutableBreakRecord.value = breakRecord
//    }

    fun setListOfDevices(devices: ArrayList<BluetoothDevice>) {
        mutableListOfDevices.value = devices
    }


}