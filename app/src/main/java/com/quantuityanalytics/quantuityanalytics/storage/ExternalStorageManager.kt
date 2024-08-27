package com.quantuityanalytics.quantuityanalytics.storage

import com.quantuityanalytics.quantuityanalytics.model.BreakRecord
import java.util.ArrayList

class ExternalStorageManager {
    companion object {
        const val TAG: String = "QuantuityAnalytics.ExternalStorageManager"
    }

    fun validatePermissions(): Boolean {
        return true
    }

    fun exportData(arrayList: ArrayList<BreakRecord>): Boolean {
        return true
    }



}