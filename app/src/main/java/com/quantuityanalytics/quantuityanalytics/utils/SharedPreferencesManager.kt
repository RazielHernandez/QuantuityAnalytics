package com.quantuityanalytics.quantuityanalytics.utils

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.quantuityanalytics.quantuityanalytics.model.SensorGroup

class SharedPreferencesManager(private val context: Context) {

    companion object {
        const val TAG = "QuantuityAnalytics.SharedPreferencesManager"
        const val SP_ADDRESSES_KEY = "ListOfSensors"
        const val SP_GROUP_ADDRESS_KEY = "ListOfGroups"
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

    fun saveArrayList(list: ArrayList<String>, key: String) {
        val json = gson.toJson(list)
        sharedPreferences.edit().putString(key, json).apply()
    }

    fun getArrayList(key: String) : ArrayList<String> {
        val json = sharedPreferences.getString(key, null)
        val type = object : TypeToken<ArrayList<String>>() {}.type
        return gson.fromJson(json, type) ?: ArrayList()
    }

    fun deleteGroup(group: SensorGroup, key: String) {
        val groupArray = getGroupArrayList(key)
        groupArray.remove(group)
        saveGroupArrayList(groupArray, key)
    }

    fun updateGroup(group: SensorGroup, key: String) {
        val groupArray = getGroupArrayList(key)
        groupArray.remove(group)
        groupArray.add(group)
        saveGroupArrayList(groupArray, key)
    }

    fun saveGroupArrayList(list: ArrayList<SensorGroup>, key: String) {
        val json = gson.toJson(list)
        sharedPreferences.edit().putString(key, json).apply()
    }

    fun getGroupArrayList(key: String): ArrayList<SensorGroup> {
        val json = sharedPreferences.getString(key, null)
        val type = object : TypeToken<ArrayList<SensorGroup>>() {}.type
        return gson.fromJson(json, type) ?: ArrayList()
    }

    fun getStringArrayList(key: String): ArrayList<String> {
        val listOfGroups = getGroupArrayList(key)
        val result = ArrayList<String> ()
        for (group in listOfGroups) {
            Log.d(TAG, "Group ${group.name} has ${group.listOfAddresses.size} addresses")
            if (group.isSelected) {
                Log.d(TAG, "Adding ${group.listOfAddresses.size} addresses from group ${group.name}")
                result.addAll(group.listOfAddresses)
            }
        }
        return result
    }

}