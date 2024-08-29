package com.quantuityanalytics.quantuityanalytics.model

import android.content.Context
import com.quantuityanalytics.quantuityanalytics.R

data class TestStep(
    val description: String,
    val image:Int,
) {
    companion object {
        private val names: MutableList<TestStep> = mutableListOf()
        fun getTestSteps(context: Context):List<TestStep>{
            names.clear()
            names.add(TestStep(context.resources.getString(R.string.step_1),
                R.drawable.break_pedal_1))
            names.add(TestStep(context.resources.getString(R.string.step_2),
                R.drawable.break_pedal_1))
            names.add(TestStep(context.resources.getString(R.string.step_3),
                R.drawable.break_pedal_1))
            return names
        }
    }
}
