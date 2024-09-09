package com.quantuityanalytics.quantuityanalytics

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.GridView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import com.google.android.material.button.MaterialButton
import com.quantuityanalytics.quantuityanalytics.adapters.GaugeViewAdapter
import com.quantuityanalytics.quantuityanalytics.ble.BleManager
import com.quantuityanalytics.quantuityanalytics.breaktest.BreakTestActivity.Companion.BLUETOOTH_ENABLE_CODE
import com.quantuityanalytics.quantuityanalytics.viewmodel.BreakViewModel

class RealTimeTestActivity: AppCompatActivity() {

    private var bleManager: BleManager? = null
    private val testViewModel: BreakViewModel by viewModels()
    private val viewAdapter: GaugeViewAdapter = GaugeViewAdapter(this, arrayListOf())

    companion object{
        const val TAG: String = "QuantuityAnalytics.RealTimeTestActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_real_time_test)

        val gridView: GridView = findViewById(R.id.gridView)
        gridView.adapter = viewAdapter

        findViewById<MaterialButton>(R.id.btn_scan).setOnClickListener {
            Log.d(TAG, "Start scanning")
            bleManager?.startScanning()
        }

        findViewById<MaterialButton>(R.id.btn_connect).setOnClickListener {
            findViewById<MaterialButton>(R.id.btn_scan).isEnabled = false
            bleManager?.connectToDeviceToWrite(testViewModel.listOfDevices.value, BleManager.COMMAND_START)
        }

        findViewById<MaterialButton>(R.id.btn_test).setOnClickListener {
            bleManager?.connectToDeviceToRead(testViewModel.listOfDevices.value)
        }

        startBluetoothManager()

        testViewModel.listOfDevices.observe(this, Observer { list ->
            Log.d(TAG, "List of device updated with ${list.size} sensors")


        })

        testViewModel.listOfRecords.observe(this, Observer { list ->
            Log.d(TAG, "List of records observer with ${list.size} sensors")
            viewAdapter.clearItems()
            viewAdapter.addItems(list)
        })
    }

    private fun startBluetoothManager() {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter
        if (bluetoothAdapter != null) {
            if (!bluetoothAdapter.isEnabled) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    finish()
                    return
                }
                startActivityForResult(enableBtIntent, BLUETOOTH_ENABLE_CODE)
            } else {
                bleManager = BleManager(this,bluetoothAdapter, testViewModel)
            }
        }
    }

}