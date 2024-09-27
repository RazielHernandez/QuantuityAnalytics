package com.quantuityanalytics.quantuityanalytics.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.quantuityanalytics.quantuityanalytics.model.SensorMacAddress

class SharedPreferencesManager(private val context:
                               Context) {

    companion object {
        const val SP_ADDRESSES_KEY = "ListOfSensors"
        const val SP_DEVICE_NAME_KEY = "DeviceName"
    }

    private val sharedPreferences =
        context.getSharedPreferences("QuantuityAnalytics", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveString(value: String, key: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun getString(key: String): String {
        return sharedPreferences.getString(key, null) ?: "UniqueDeviceNAme"
    }

    fun saveArrayList(list: ArrayList<SensorMacAddress>, key: String) {
        val json = gson.toJson(list)
        sharedPreferences.edit().putString(key, json).apply()
    }

    fun getArrayList(key: String): ArrayList<SensorMacAddress> {
        val json = sharedPreferences.getString(key, null)
        val type = object : TypeToken<ArrayList<SensorMacAddress>>() {}.type
        return gson.fromJson(json, type) ?: ArrayList()
    }

    fun getStringArrayList(key: String): ArrayList<String> {
        val json = sharedPreferences.getString(key, null)
        val type = object : TypeToken<ArrayList<SensorMacAddress>>() {}.type
        val list = gson.fromJson(json, type) ?: ArrayList<SensorMacAddress>()
        val result = ArrayList<String> ()
        for (element in list) {
            result.add(element.address)
        }
        return result
    }

}