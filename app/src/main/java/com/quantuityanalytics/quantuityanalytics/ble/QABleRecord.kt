package com.quantuityanalytics.quantuityanalytics.ble

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
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

    private var previousSpeed: Float = 0f,
) {

    fun printToCSV(): String {
        return "$timeStamp,$deviceId,$truckId,$sensorId,$breakRecord,$value"
    }

    fun getColorResource(model: String, context: Context): Int {
        var color = ResourcesCompat.getColor(context.resources, R.color.white, null)
        if (model == "4 values model") {
            if (breakRecord.contains("d1")) {
                color = ResourcesCompat.getColor(context.resources, R.color.red, null)
            } else if (breakRecord.contains("d2")) {
                color = ResourcesCompat.getColor(context.resources, R.color.yellow, null)
            } else if (breakRecord.contains("d3")) {
                color = ResourcesCompat.getColor(context.resources, R.color.green, null)
            } else if (breakRecord.contains("d4")) {
                color = ResourcesCompat.getColor(context.resources, R.color.green, null)
            }
        } else if (model == "2 values model") {
            if (breakRecord.contains("off")) {
                color = ResourcesCompat.getColor(context.resources, R.color.green, null)
            } else if (breakRecord.contains("on")) {
                color = ResourcesCompat.getColor(context.resources, R.color.red, null)
            }
        } else if (model == "3 values model") {
            if (breakRecord.contains("off")) {
                color = ResourcesCompat.getColor(context.resources, R.color.green, null)
            } else if (breakRecord.contains("on")) {
                color = ResourcesCompat.getColor(context.resources, R.color.red, null)
            }
        }

        return color
    }

    fun getColoredIcon(model: String, context: Context): Drawable? {
        var icon = ResourcesCompat.getDrawable(context.resources, R.drawable.warningsignal, null)
        if (model == "4 values model") {
            if (breakRecord.contains("d1")) {
                icon = ResourcesCompat.getDrawable(context.resources, R.drawable.break_red, null)
            } else if (breakRecord.contains("d2")) {
                icon = ResourcesCompat.getDrawable(context.resources, R.drawable.break_yellow, null)
            } else if (breakRecord.contains("d3")) {
                icon = ResourcesCompat.getDrawable(context.resources, R.drawable.break_green, null)
            } else if (breakRecord.contains("d4")) {
                icon = ResourcesCompat.getDrawable(context.resources, R.drawable.break_green, null)
            }
        } else if (model == "2 values model") {
            if (breakRecord.contains("off")) {
                icon = ResourcesCompat.getDrawable(context.resources, R.drawable.break_green, null)
            } else if (breakRecord.contains("on")) {
                icon = ResourcesCompat.getDrawable(context.resources, R.drawable.break_red, null)
            }
        } else if (model == "3 values model") {
            if (breakRecord.contains("off")) {
                icon = ResourcesCompat.getDrawable(context.resources, R.drawable.break_green, null)
            } else if (breakRecord.contains("d3")) {
                icon = ResourcesCompat.getDrawable(context.resources, R.drawable.break_green, null)
            } else if (breakRecord.contains("on")) {
                icon = ResourcesCompat.getDrawable(context.resources, R.drawable.break_red, null)
            }
        }

        return icon
    }

    fun getTestResult(model: String): String {
        var result = "Unknown"
        if (model == "4 values model") {
            if (breakRecord.contains("d1")) { result = "Out of service" }
            else if (breakRecord.contains("d2")) { result = "Maintenance" }
            else if (breakRecord.contains("d3")) { result = "Pass" }
            else if (breakRecord.contains("d4")) { result = "Pass" }
        } else if (model == "2 values model") {
            if (breakRecord.contains("off")) { result = "Pass" }
            else if (breakRecord.contains("on")) { result = "Fail" }
        } else if (model == "3 values model") {
            if (breakRecord.contains("off")) { result = "Pass" }
            else if (breakRecord.contains("d3")) { result = "Pass" }
            else if (breakRecord.contains("on")) { result = "Fail" }
        }

        return result
    }

    fun getSpeed(model: String): Float {
        var result = 0f
        if (model == "4 values model") {
            if (breakRecord.contains("d1")) { result = 10f }
            else if (breakRecord.contains("d2")) { result = 7f }
            else if (breakRecord.contains("d3")) { result = 4f }
            else if (breakRecord.contains("d4")) { result = 1f }
        } else if (model == "2 values model") {
            if (breakRecord.contains("off")) {
                if (previousSpeed < 1) {
                    result = 1f
                } else {
                    result = 3f
                    previousSpeed = 3f
                }

            }
            else if (breakRecord.contains("on")) { result = 9f }
        } else if (model == "3 values model") {
            if (breakRecord.contains("off")) { result = 1f }
            else if (breakRecord.contains("d3")) { result = 5f }
            else if (breakRecord.contains("on")) { result = 9f }
        }
        return result
    }

    fun createNote(model: String, context: Context, speedView: SpeedView, text: String): TextNote {
        val note = TextNote(context, text)
            .setPosition(Note.Position.TopIndicator)
            .setAlign(Note.Align.Bottom)
            .setTextTypeFace(Typeface.create(Typeface.DEFAULT, Typeface.BOLD))
            .setBackgroundColor(getColorResource(model, context))
            .setCornersRound(20f)
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

        const val MAX_SPEED = 10f

    }

}