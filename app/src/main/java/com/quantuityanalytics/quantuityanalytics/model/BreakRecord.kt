package com.quantuityanalytics.quantuityanalytics.model

import java.util.UUID

data class BreakRecord(
    val id: UUID = UUID.randomUUID(),
    val timeStamp: String,
    val truckId: String,
    val sensorId: String = "000x1",
    val breakRecord: String = "default",
    val value: Float = 0f,
) {
    fun printToCSV(): String {
        return "$timeStamp,$truckId,$sensorId,$breakRecord,$value"
    }
}
