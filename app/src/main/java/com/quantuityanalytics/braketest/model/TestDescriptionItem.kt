package com.quantuityanalytics.braketest.model

import android.content.Context
import com.quantuityanalytics.braketest.R

data class TestDescriptionItem (
    val description: String,
    val image:Int,
) {
    companion object {
        private val names: MutableList<TestDescriptionItem> = mutableListOf()
        fun getTestDescriptionSteps(context: Context):List<TestDescriptionItem>{
            names.clear()
            names.add(TestDescriptionItem(context.resources.getString(R.string.test_description_1),R.drawable.truck1))
            names.add(TestDescriptionItem(context.resources.getString(R.string.test_description_2),R.drawable.truck2))
            names.add(TestDescriptionItem(context.resources.getString(R.string.test_description_3),R.drawable.truck3))
            return names
        }
    }

}