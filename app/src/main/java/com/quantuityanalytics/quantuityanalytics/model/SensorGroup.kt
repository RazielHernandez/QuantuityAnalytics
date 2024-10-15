package com.quantuityanalytics.quantuityanalytics.model

import java.util.UUID

data class SensorGroup(
    val id: UUID = UUID.randomUUID(),
    var name: String,
    var isSelected: Boolean,
    var listOfAddresses: ArrayList<String>,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SensorGroup) return false

        return this.id == other.id
    }

    override fun hashCode(): Int {
        val result = id.hashCode()
        return result
    }
}
