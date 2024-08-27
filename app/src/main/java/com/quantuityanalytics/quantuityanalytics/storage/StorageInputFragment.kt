package com.quantuityanalytics.quantuityanalytics.storage

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.button.MaterialButton
import com.quantuityanalytics.quantuityanalytics.R
import com.quantuityanalytics.quantuityanalytics.model.BreakRecord
import com.quantuityanalytics.quantuityanalytics.viewmodel.StorageViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class StorageInputFragment: Fragment(R.layout.fragment_storage_input) {

    companion object {
        const val TAG: String = "QuantuityAnalytics.StorageInputFragment"
    }

    private val storageViewModel: StorageViewModel by activityViewModels()
    private var localStorageManager: LocalStorageManager? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val saveButton: MaterialButton = view.findViewById(R.id.btn_save)
        saveButton.setOnClickListener {
            val records = createRecords()
            if (records.size > 0) {
                createFile(records)
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        localStorageManager = LocalStorageManager(context)
    }


    private fun createRecords(): ArrayList<BreakRecord> {
        val testName: EditText? = view?.findViewById(R.id.record_test)
        val testSensor: EditText? = view?.findViewById(R.id.record_sensor)
        val testString: EditText? = view?.findViewById(R.id.record_string)
        val testInt: EditText? = view?.findViewById(R.id.record_int)
        val testCounter: EditText? = view?.findViewById(R.id.record_counter)

        val result: ArrayList<BreakRecord> = arrayListOf()

        val numberOfRecords = Integer.parseInt(testCounter?.text.toString())
        if (numberOfRecords > 0) {
            while (result.size < numberOfRecords) {
                val tsLong = System.currentTimeMillis()
                val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.ENGLISH)
                val dateTime = simpleDateFormat.format(tsLong).toString()
                result.add(
                    BreakRecord(
                        timeStamp = dateTime,
                        testId = testName?.text.toString(),
                        sensorId = testSensor?.text.toString(),
                        breakRecord = testString?.text.toString(),
                        value = Integer.parseInt(testInt?.text.toString())
                    )
                )
            }
        }
        return result
    }

    private fun createFile(records: ArrayList<BreakRecord>) {
        val tsLong = System.currentTimeMillis() / 1000
        val ts = tsLong.toString()
        val fileName = "Storage_${ts}_${records.size}_QuantuityAnalytics.json"
        localStorageManager?.saveRecords(fileName,records.toTypedArray(), true)
        storageViewModel.setUpdate(true)
        clearForm()
    }

    private fun clearForm() {
        val testName: EditText? = view?.findViewById(R.id.record_test)
        val testSensor: EditText? = view?.findViewById(R.id.record_sensor)
        val testString: EditText? = view?.findViewById(R.id.record_string)
        val testInt: EditText? = view?.findViewById(R.id.record_int)
        val testCounter: EditText? = view?.findViewById(R.id.record_counter)

        testName?.text?.clear()
        testSensor?.text?.clear()
        testString?.text?.clear()
        testInt?.text?.clear()
        testCounter?.text?.clear()
    }
}