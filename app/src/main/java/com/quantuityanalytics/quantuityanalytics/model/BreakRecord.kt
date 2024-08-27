package com.quantuityanalytics.quantuityanalytics.model

import java.util.UUID

data class BreakRecord(
    val id: UUID = UUID.randomUUID(),
    val timeStamp: String,
    val testId: String,
    val sensorId: String = "000x1",
    val breakRecord: String = "default",
    val value: Int = 0,
) {
    fun printToCSV(): String {
        return "$timeStamp,$testId,$sensorId,$breakRecord,$value"
    }
}
