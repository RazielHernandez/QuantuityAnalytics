package com.quantuityanalytics.quantuityanalytics.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.quantuityanalytics.quantuityanalytics.model.BreakRecord

class BreakViewModel: ViewModel() {

    private var mutableBreakRecord = MutableLiveData<BreakRecord>()
    val breakRecord: LiveData<BreakRecord> get() = mutableBreakRecord

    fun setBreakRecord(breakRecord: BreakRecord) {
        mutableBreakRecord.value = breakRecord
    }
}