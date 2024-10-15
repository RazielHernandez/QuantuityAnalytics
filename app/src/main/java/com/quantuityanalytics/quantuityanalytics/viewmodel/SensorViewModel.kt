package com.quantuityanalytics.quantuityanalytics.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.quantuityanalytics.quantuityanalytics.model.SensorGroup

class SensorViewModel: ViewModel() {

    private var mutableListOfGroups = MutableLiveData<List<SensorGroup>>()
    val listOfGroups: LiveData<List<SensorGroup>> get() = mutableListOfGroups

    private var mutableGroup = MutableLiveData<SensorGroup>()
    val group: LiveData<SensorGroup> get() = mutableGroup

    private var mutableCloseAction = MutableLiveData<Boolean>()
    val closeAction: LiveData<Boolean> get() = mutableCloseAction

    fun setListOfGroups(groups: List<SensorGroup>) {
        mutableListOfGroups.value = groups
    }

    fun setGroup(group: SensorGroup) {
        mutableGroup.value = group
    }

    fun setCloseAction(action: Boolean) {
        mutableCloseAction.value = action
    }
}