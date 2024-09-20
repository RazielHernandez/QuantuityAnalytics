package com.quantuityanalytics.quantuityanalytics.breaktest

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.quantuityanalytics.quantuityanalytics.MenuActivity
import com.quantuityanalytics.quantuityanalytics.R
import com.quantuityanalytics.quantuityanalytics.RealTimeTestActivity
import com.quantuityanalytics.quantuityanalytics.RealTimeTestActivity.Companion
import com.quantuityanalytics.quantuityanalytics.ble.BleDeviceManager
import com.quantuityanalytics.quantuityanalytics.ble.QABleDevice
import com.quantuityanalytics.quantuityanalytics.ble.QABleRecord
import com.quantuityanalytics.quantuityanalytics.storage.LocalStorageManager
import com.quantuityanalytics.quantuityanalytics.viewmodel.BreakViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class BreakTestActivity: AppCompatActivity() {

    companion object {
        const val TAG: String = "QuantuityAnalytics.TestActivity"
    }

    private var localStorageManager: LocalStorageManager = LocalStorageManager(this)
    private val testViewModel: BreakViewModel by viewModels()

    private val fragmentScanner = BreakTestScannerFragment()
    private val fragmentDevices = BreakTestDevicesFragment()
    private val fragmentStep = BreakTestStepFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_break_test)

        loadFragment(R.id.fragment_devices, fragmentDevices)
        loadFragment(R.id.fragment_container, fragmentScanner)

        testViewModel.startAction.observe(this, Observer { value ->
            if (value) {
                loadFragment(R.id.fragment_container, fragmentStep)
            } else {

                val devices = testViewModel.listOfDevices.value
                if (devices != null) {
                    val records = arrayListOf<QABleRecord>()
                    for (device in devices) {
                        records.addAll(device.listOfRecords)
                    }

                    if (records.size > 0) {
                        createFile(records)
                    }
                    Log.d(TAG,"Saving ${records.size} records for this session")

                } else {
                    Log.d(TAG, "No device connected to save data")
                }

            }
        })

    }

    private fun createFile(records: ArrayList<QABleRecord>) {
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
        val current = LocalDateTime.now().format(formatter)
        val fileName = "QuantuityAnalytics_${current}_${records.size}.json"
        localStorageManager.saveRecords(fileName,records.toTypedArray(), true)
        showSnackBar("File saved successfully. You can see it in Settings/Files menu")
    }

    private fun showSnackBar(msg: String) {
        val layoutMain: ConstraintLayout = findViewById(R.id.main)
        val snack: Snackbar = Snackbar.make(layoutMain, msg, Snackbar.LENGTH_LONG)
            .setBackgroundTint(resources.getColor(R.color.primary_light))
            .setTextColor(resources.getColor(R.color.white))
        val view = snack.view
        val params = view.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        params.width = FrameLayout.LayoutParams.FILL_PARENT
        view.layoutParams = params
        snack.show()
    }

    private fun loadFragment(frame: Int, fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setReorderingAllowed(true)
            .replace(frame, fragment)
            .commit()
    }
}