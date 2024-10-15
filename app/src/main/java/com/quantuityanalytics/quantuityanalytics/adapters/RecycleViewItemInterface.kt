package com.quantuityanalytics.quantuityanalytics.adapters

import android.util.Log

interface RecycleViewItemInterface {
    fun onDeviceClick(position: Int)

    fun onDeviceSelected(position: Int, isChecked: Boolean) {
        Log.d("RecycleViewItemInterface", "onSelectDevice not implemented")
    }
}