package com.quantuityanalytics.quantuityanalytics.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.quantuityanalytics.quantuityanalytics.model.SensorGroup

class QAPreferencesConverter {

    companion object {

        private val gson = Gson()

        fun convertStringToGroupList(string: String): ArrayList<SensorGroup> {
            val type = object : TypeToken<ArrayList<SensorGroup>>() {}.type
            return gson.fromJson(string, type) ?: ArrayList()
        }

        fun convertGroupListToString(list: ArrayList<SensorGroup>): String {
            return gson.toJson(list)
        }
    }

}