package com.quantuityanalytics.quantuityanalytics.model

import java.util.UUID

data class BreakRecord(
    val id: UUID = UUID.randomUUID(),
    val timeStamp: String,
    val truckId: String,
    val sensorId: String = "000x1",
    val breakRecord: String = "default",
    val value: Float = 0f,
    val status: Int = STATUS_DISCONNECTED
) {
    companion object {
        const val STATUS_CONNECTED = 1
        const val STATUS_DISCONNECTED = 0
        const val STATUS_NOT_FOUND = -1
    }

    fun printToCSV(): String {
        return "$timeStamp,$truckId,$sensorId,$breakRecord,$value"
    }
}
