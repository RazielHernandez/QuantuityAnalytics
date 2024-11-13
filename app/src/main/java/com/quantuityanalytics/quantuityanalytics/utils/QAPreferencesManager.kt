package com.quantuityanalytics.quantuityanalytics.utils

import android.content.Context
import android.content.SharedPreferences

class QAPreferencesManager(context: Context) {
    private val preferences: SharedPreferences = context.getSharedPreferences("qa_prefs", Context.MODE_PRIVATE)

    // Save a String value
    fun putString(key: String, value: String) {
        preferences.edit().putString(key, value).apply()
    }

    // Get a String value
    fun getString(key: String, defaultValue: String = ""): String {
        return preferences.getString(key, defaultValue) ?: defaultValue
    }

    // Save an Int value
    fun putInt(key: String, value: Int) {
        preferences.edit().putInt(key, value).apply()
    }

    // Get an Int value
    fun getInt(key: String, defaultValue: Int = 0): Int {
        return preferences.getInt(key, defaultValue)
    }

    // Save a Boolean value
    fun putBoolean(key: String, value: Boolean) {
        preferences.edit().putBoolean(key, value).apply()
    }

    // Get a Boolean value
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return preferences.getBoolean(key, defaultValue)
    }

    // Save a Float value
    fun putFloat(key: String, value: Float) {
        preferences.edit().putFloat(key, value).apply()
    }

    // Get a Float value
    fun getFloat(key: String, defaultValue: Float = 0f): Float {
        return preferences.getFloat(key, defaultValue)
    }

    // Remove a value
    fun remove(key: String) {
        preferences.edit().remove(key).apply()
    }

    // Clear all values
    fun clear() {
        preferences.edit().clear().apply()
    }
}