package com.quantuityanalytics.quantuityanalytics.storage

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.quantuityanalytics.quantuityanalytics.model.BreakRecord
import com.quantuityanalytics.quantuityanalytics.model.StorageFile
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class LocalStorageManager(private val context: Context) {

    companion object {
        const val TAG: String = "QuantuityAnalytics.LocalStorageManager"
    }

    fun deleteFile(fileName: String): Boolean {
        val file = File(context.filesDir, fileName)
        file.delete()
        return true
    }

    fun saveRecords(fileName: String, records: Array<BreakRecord>, overwrite: Boolean) {
        val gson = Gson()
        val dataToWrite = gson.toJson(records)
        val file = File(context.filesDir, fileName)
        if (file.exists() && !overwrite) { return }

        FileWriter(file).use {
            it.write(dataToWrite)
        }
    }

    fun getFiles(): ArrayList<StorageFile> {
        val result: ArrayList<StorageFile> = arrayListOf()
        val files = context.filesDir.listFiles()
        if (files != null){
            for (file in files) {
                if (file.name.endsWith("QuantuityAnalytics.json")) {

                    val lastModifiedDate =  Date(file.lastModified())
                    val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.ENGLISH)
                    val dateTime = simpleDateFormat.format(lastModifiedDate).toString()
                    Log.d(TAG, "File ${file.name} was created on $lastModifiedDate")

                    result.add(
                        StorageFile(name = file.name, date = dateTime )
                    )
                }
            }
        }
        return result
    }

    fun getRecords(filename: String): Array<BreakRecord>? {
        val file = File(context.filesDir, filename)
        if (!file.exists()) return null

        val gson = Gson()
        FileReader(file).use { reader ->
            val type = object : TypeToken<Array<BreakRecord>>() {}.type
            return gson.fromJson(reader, type)
        }
    }

    fun addRecords(records: Array<BreakRecord>, fileName: String){

    }

    fun deleteRecords(records: Array<BreakRecord>, fileName: String) {

    }

}