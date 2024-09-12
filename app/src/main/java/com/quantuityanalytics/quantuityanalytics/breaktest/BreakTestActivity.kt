package com.quantuityanalytics.quantuityanalytics.breaktest

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.FrameLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.quantuityanalytics.quantuityanalytics.MenuActivity
import com.quantuityanalytics.quantuityanalytics.R
import com.quantuityanalytics.quantuityanalytics.ble.BleManager
import com.quantuityanalytics.quantuityanalytics.viewmodel.BreakViewModel

class BreakTestActivity: AppCompatActivity() {

    companion object {
        const val TAG: String = "QuantuityAnalytics.TestActivity"
        const val BLUETOOTH_ENABLE_CODE: Int = 3301
    }

    private val testViewModel: BreakViewModel by viewModels()

    private val fragmentScanner = BreakTestScannerFragment()
    private val fragmentDevices = BreakTestDevicesFragment()
    private val fragmentStep = BreakTestStepFragment()

    private var bleManager: BleManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_break_test)

        loadFragment(R.id.fragment_devices, fragmentDevices)
        loadFragment(R.id.fragment_container, fragmentScanner)

        testViewModel.scannerStatus.observe(this, Observer { value ->
            if (value) {
                try {
                    bleManager?.startScanning()
                } catch (ex: IllegalStateException) {
                    Log.d(TAG,"Error RECEIVED")
                    finish()
                }
            }
        })


        testViewModel.startAction.observe(this, Observer { value ->
            if (value) {
                bleManager?.connectToDeviceToWrite(testViewModel.listOfDevices.value, BleManager.COMMAND_STOP)

                loadFragment(R.id.fragment_container, fragmentStep)
                //bleManager?.connectToDeviceToRead(testViewModel.listOfDevices.value)
            }
        })


    }

    @SuppressLint("MissingPermission")
    override fun onStart() {
        super.onStart()
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter
        if (bluetoothAdapter != null) {
            if (!bluetoothAdapter.isEnabled) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, BLUETOOTH_ENABLE_CODE)
            } else {
                bleManager = BleManager(this,bluetoothAdapter, testViewModel)
                testViewModel.setScannerStatus(true)
            }
        }
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
                bleManager = BleManager(this,bluetoothAdapter, testViewModel)
                testViewModel.setScannerStatus(true)
            }
        }
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