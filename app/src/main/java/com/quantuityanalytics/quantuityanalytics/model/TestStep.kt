package com.quantuityanalytics.quantuityanalytics.model

import android.content.Context
import com.quantuityanalytics.quantuityanalytics.R

data class TestStep(
    val description: String,
    val image: Int,
    val seconds: Int,
) {
    companion object {
        private val names: ArrayList<TestStep> = arrayListOf()
        fun getTestSteps(context: Context):ArrayList<TestStep>{
            names.clear()
            names.add(TestStep(context.resources.getString(R.string.step_1),
                R.drawable.break_pedal_1,2000))
            names.add(TestStep(context.resources.getString(R.string.step_2),
                R.drawable.break_pedal_1,3000))
            names.add(TestStep(context.resources.getString(R.string.step_3),
                R.drawable.break_pedal_1, 5000))
            return names
        }
    }
}
