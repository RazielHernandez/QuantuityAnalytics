package com.quantuityanalytics.quantuityanalytics.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.quantuityanalytics.quantuityanalytics.model.BreakRecord
import com.quantuityanalytics.quantuityanalytics.model.StorageFile

class StorageViewModel: ViewModel() {

    private var mutableFileList = MutableLiveData<List<StorageFile>>()
    val fileList: LiveData<List<StorageFile>> get() = mutableFileList

    private var mutableFileName = MutableLiveData<String>()
    val fileName: LiveData<String> get() = mutableFileName

    private var mutableUpdate = MutableLiveData<Boolean>()
    val update: LiveData<Boolean> get() = mutableUpdate

    fun setFileName(name: String) {
        mutableFileName.value = name
    }

    fun setUpdate(value: Boolean) {
        mutableUpdate.value = value
    }

}