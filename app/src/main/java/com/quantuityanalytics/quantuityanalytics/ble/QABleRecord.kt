package com.quantuityanalytics.quantuityanalytics.ble

import com.quantuityanalytics.quantuityanalytics.model.BreakRecord.Companion.STATUS_DISCONNECTED

data class QABleRecord (
    val timeStamp: String,
    val truckId: String,
    val sensorId: String = "000x1",
    val breakRecord: String = "default",
    val value: Float = 0f,
) {

    fun printToCSV(): String {
        return "$timeStamp,$truckId,$sensorId,$breakRecord,$value"
    }



    companion object {

        fun getDefaultRecord(sensorId: String = "Default Sensor ID"): QABleRecord {
            return QABleRecord(
                timeStamp = "",
                truckId = "TruckId",
                sensorId = sensorId,
                breakRecord = "Disconnected",
                value = 0f
            )
        }

    }

}