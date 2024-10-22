package com.quantuityanalytics.quantuityanalytics.ble

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.Log
import androidx.core.content.res.ResourcesCompat
import com.github.anastr.speedviewlib.SpeedView
import com.github.anastr.speedviewlib.components.note.Note
import com.github.anastr.speedviewlib.components.note.TextNote
import com.quantuityanalytics.quantuityanalytics.R


data class QABleRecord (
    val timeStamp: String,
    val deviceId: String,
    val truckId: String,
    val sensorId: String = "000x1",
    val breakRecord: String = "default",
    val value: Float = 0f,
) {

    fun printToCSV(): String {
        return "$timeStamp,$truckId,$sensorId,$breakRecord,$value"
    }

    fun getColorResource(context: Context): Int {
        var color = ResourcesCompat.getColor(context.resources, R.color.white, null)
        if (breakRecord.contains("d1")) {
            color = ResourcesCompat.getColor(context.resources, R.color.red, null)
        } else if (breakRecord.contains("d2")) {
            color = ResourcesCompat.getColor(context.resources, R.color.yellow, null)
        } else if (breakRecord.contains("d3")) {
            color = ResourcesCompat.getColor(context.resources, R.color.green, null)
        } else if (breakRecord.contains("d4")) {
            color = ResourcesCompat.getColor(context.resources, R.color.green, null)
        }
        return color
    }

    fun getTestResult(): String {
        var result = "Unknown"
        if (breakRecord.contains("d1")) {
            result = "Out of service"
        } else if (breakRecord.contains("d2")) {
            result = "Maintenance"
        } else if (breakRecord.contains("d3")) {
            result = "Pass"
        } else if (breakRecord.contains("d4")) {
            result = "Pass"
        }
        return result
    }

    fun createNote(context: Context, speedView: SpeedView, text: String): TextNote {
        val note = TextNote(context, text)
            .setPosition(Note.Position.TopIndicator) // position of Note.
            .setAlign(Note.Align.Bottom) // Note Align.
            .setTextTypeFace(Typeface.create(Typeface.DEFAULT, Typeface.BOLD)) // style, or font.
            .setBackgroundColor(getColorResource(context)) // change dialog color.
            .setCornersRound(20f) // dialog's rectangle Corners Round.
            .setTextSize(speedView.dpTOpx(14f))
        return note
    }

    companion object {

        fun getDefaultRecord(sensorId: String = "Default Sensor ID"): QABleRecord {
            return QABleRecord(
                timeStamp = "",
                deviceId = "DeviceId",
                truckId = "TruckId",
                sensorId = sensorId,
                breakRecord = "",
                value = 0f
            )
        }



    }

}