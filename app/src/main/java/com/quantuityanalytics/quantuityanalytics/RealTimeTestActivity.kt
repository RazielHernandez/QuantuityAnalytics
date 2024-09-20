package com.quantuityanalytics.quantuityanalytics

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.GridView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import com.google.android.material.button.MaterialButton
import com.quantuityanalytics.quantuityanalytics.adapters.GaugeViewAdapter
import com.quantuityanalytics.quantuityanalytics.ble.BleDeviceManager
import com.quantuityanalytics.quantuityanalytics.ble.QABleRecord
import com.quantuityanalytics.quantuityanalytics.storage.LocalStorageManager
import com.quantuityanalytics.quantuityanalytics.viewmodel.BreakViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class RealTimeTestActivity: AppCompatActivity() {


    private var bleDeviceManager: BleDeviceManager? = null
    private val testViewModel: BreakViewModel by viewModels()
    private val viewAdapter: GaugeViewAdapter = GaugeViewAdapter(this, arrayListOf())

    private var localStorageManager: LocalStorageManager = LocalStorageManager(this)

    companion object{
        const val TAG: String = "QuantuityAnalytics.RealTimeTestActivity"

        const val BLUETOOTH_ENABLE_CODE: Int = 3301
    }

    private var scanning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_real_time_test)

        val gridView: GridView = findViewById(R.id.gridView)
        gridView.adapter = viewAdapter

        val saveButton: MaterialButton = findViewById(R.id.btn_save)
        val actionButton: MaterialButton = findViewById(R.id.btn_start)
        actionButton.setOnClickListener {
            scanning = !scanning
            if (scanning) {
                testViewModel.listOfDevices.value?.let { list ->
                    bleDeviceManager?.connectToGetUpdate(list)
                }
                actionButton.text = resources.getText(R.string.button_stop)
                actionButton.icon = ResourcesCompat.getDrawable(this.resources, R.drawable.baseline_stop_circle_24, null)
                saveButton.isEnabled = false
            } else {
                bleDeviceManager?.disconnectFromAllDevices()
                actionButton.text = resources.getText(R.string.button_start)
                actionButton.icon = ResourcesCompat.getDrawable(this.resources, R.drawable.baseline_play_outline_24, null)
                saveButton.isEnabled = true
            }
        }


        saveButton.setOnClickListener {
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

        findViewById<MaterialButton>(R.id.btn_close).setOnClickListener {
            finish()
            bleDeviceManager?.disconnectFromAllDevices()
        }

        testViewModel.listOfDevices.observe(this, Observer { list ->
            Log.d(TAG, "List of device updated with ${list.size} sensors")
            viewAdapter.clearItems()
            viewAdapter.addItems(list)
        })
    }

    override fun onStart() {
        super.onStart()
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
                    Log.d(TAG, "Not enough permissions")
                    finish()
                    return
                }
                startActivityForResult(enableBtIntent, BLUETOOTH_ENABLE_CODE)
            } else {
                Log.d(TAG, "On Start() init BleDeviceManager")
                val list = arrayListOf<String>()
                list.add("B4:3A:31:EF:52:8B")
                list.add("B4:3A:31:EF:52:8C")
                bleDeviceManager = BleDeviceManager(this,bluetoothAdapter, list ,testViewModel)
                bleDeviceManager?.startScanning()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bleDeviceManager.let {
            Log.d(TAG, "Disconnecting from all devices before destroy")
            bleDeviceManager?.disconnectFromAllDevices()
        }
    }

    fun Activity.onBackButtonPressed(callback: (() -> Boolean)) {
        (this as? FragmentActivity)?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (!callback()) {
                    bleDeviceManager.let {
                        Log.d(TAG, "Disconnecting from all devices before go back")
                        bleDeviceManager?.disconnectFromAllDevices()
                    }
                    remove()
                    performBackPress()
                }
            }
        })
    }

    fun Activity.performBackPress() {
        (this as? FragmentActivity)?.onBackPressedDispatcher?.onBackPressed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == BLUETOOTH_ENABLE_CODE) {
            if (resultCode == 0) {
                finish()
                val intent = Intent(this, MenuActivity::class.java)
                startActivity(intent)
            }else {
                val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
                val bluetoothAdapter = bluetoothManager.adapter
                val list = arrayListOf<String>()
                list.add("B4:3A:31:EF:52:8B")
                list.add("B4:3A:31:EF:52:8C")
                bleDeviceManager = BleDeviceManager(this,bluetoothAdapter, list ,testViewModel)
                bleDeviceManager?.startScanning()
            }
        }
    }

    private fun createFile(records: ArrayList<QABleRecord>) {
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
        val current = LocalDateTime.now().format(formatter)
        val fileName = "QuantuityAnalytics_${current}_${records.size}.json"
        localStorageManager.saveRecords(fileName,records.toTypedArray(), true)
        Toast.makeText(this,"File saved successfully", Toast.LENGTH_SHORT).show()
    }

}