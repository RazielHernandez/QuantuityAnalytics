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
import com.quantuityanalytics.quantuityanalytics.ble.BleDeviceManager
import com.quantuityanalytics.quantuityanalytics.ble.BleManager
import com.quantuityanalytics.quantuityanalytics.breaktest.BreakTestActivity
import com.quantuityanalytics.quantuityanalytics.breaktest.BreakTestActivity.Companion
import com.quantuityanalytics.quantuityanalytics.breaktest.BreakTestActivity.Companion.BLUETOOTH_ENABLE_CODE
import com.quantuityanalytics.quantuityanalytics.viewmodel.BreakViewModel

class RealTimeTestActivity: AppCompatActivity() {

    //private var bleManager: BleManager? = null
    private var bleDeviceManager: BleDeviceManager? = null
    private val testViewModel: BreakViewModel by viewModels()
    private val viewAdapter: GaugeViewAdapter = GaugeViewAdapter(this, arrayListOf())

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

//        findViewById<MaterialButton>(R.id.btn_scan).setOnClickListener {
//            Log.d(TAG, "Start scanning")
//            //bleManager?.startScanning()
//            bleDeviceManager?.startScanning()
//        }

        findViewById<MaterialButton>(R.id.btn_start).setOnClickListener {
            scanning = !scanning
            if (scanning) {
                testViewModel.listOfDevices.value?.let { list ->
                    bleDeviceManager?.connectListOfDevices(list)
                }
                findViewById<MaterialButton>(R.id.btn_start).text = resources.getText(R.string.button_stop)
            } else {
                bleDeviceManager?.disconnectFromAllDevices()
                findViewById<MaterialButton>(R.id.btn_start).text = resources.getText(R.string.button_start)
            }


            //bleManager?.connectToDeviceToWrite(testViewModel.listOfDevices.value, BleManager.COMMAND_START)


        }

//        if (bleDeviceManager != null) {
//            bleDeviceManager?.startScanning()
//            Log.d(TAG, "start scanning in activity")
//        } else {
//            Log.d(TAG, "Can´t start scanning")
//        }


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
                startActivityForResult(enableBtIntent, BreakTestActivity.BLUETOOTH_ENABLE_CODE)
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
        bleDeviceManager?.disconnectAllDevices()
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

}