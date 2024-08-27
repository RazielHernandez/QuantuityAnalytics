package com.quantuityanalytics.quantuityanalytics.storage

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.quantuityanalytics.quantuityanalytics.R
import com.quantuityanalytics.quantuityanalytics.viewmodel.StorageViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class StorageRecordsFragment: Fragment(R.layout.fragment_storage_records) {

    private var localStorageManager: LocalStorageManager? = null
    private val storageViewModel: StorageViewModel by activityViewModels()

    private var textView: TextView? = null
    private var deleteButton: Button? = null
    private var exportButton: Button? = null
    private var fileName: String = ""

    companion object {
        const val TAG: String = "QuantuityAnalytics.StorageRecordsFragment"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textView = view.findViewById(R.id.records)
        deleteButton = view.findViewById(R.id.btn_delete)
        exportButton = view.findViewById(R.id.btn_export)

        storageViewModel.fileName.observe(viewLifecycleOwner, Observer { name ->
            getRecords(name)
        })

        deleteButton?.setOnClickListener {
            if (fileName.isNotEmpty()) {
                localStorageManager?.deleteFile(fileName)
                storageViewModel.setUpdate(true)
                clearData()
            }
        }

        exportButton?.setOnClickListener {
            if(fileName.isNotEmpty()) {
                exportToDownloads()
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        localStorageManager = LocalStorageManager(context)
    }

    private fun getRecords(fileName: String) {
        val records = localStorageManager?.getRecords(fileName)
        if (records != null && textView != null) {
            this.fileName = fileName
            var fileText = ""
            for (record in records) {
                fileText = "$fileText${record.printToCSV()}\n"
            }
            textView?.text = fileText
        } else {
            Log.d(TAG, "No records were founded in file $fileName")
            textView?.text = "No records were founded in file"
        }
    }

    private fun exportToDownloads() {
        try {
            val downloadsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

            val exportFileName = fileName.replace(".json",".csv")
            val file = File(downloadsDirectory, exportFileName)

            FileOutputStream(file).use { outputStream ->
                outputStream.write(textView?.getText().toString().toByteArray())
            }

            Toast.makeText(context,"The file was exported to Download folder", Toast.LENGTH_SHORT).show()
            clearData()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context,"Error while trying to export file", Toast.LENGTH_SHORT).show()
        }


    }

    // writeTextData() method save the data into the file in byte format
    // It also toast a message "Done/filepath_where_the_file_is_saved"
    private fun writeTextData(file: File, data: String) {
        var fileOutputStream: FileOutputStream? = null
        try {
            fileOutputStream = FileOutputStream(file)
            fileOutputStream.write(data.toByteArray())
            Toast.makeText(context, "Done" + file.absolutePath, Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun clearData() {
        textView?.text = "No file selected"
        this.fileName = ""
    }
}