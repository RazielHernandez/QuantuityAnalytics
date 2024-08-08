package com.quantuityanalytics.quantuityanalytics

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.quantuityanalytics.quantuityanalytics.ble.BleDeviceAdapter
import com.quantuityanalytics.quantuityanalytics.ble.BleManager

class TestActivity: AppCompatActivity() {

    companion object {
        val TAG: String = "QuantuityAnalytics.TestActivity"
    }

    private val deviceAdapter = BleDeviceAdapter(context = this, data = arrayListOf())
    private val bleManager: BleManager = BleManager(this, deviceAdapter)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_brake_test)

        val scanButton: MaterialButton = findViewById(R.id.btn_scan)
        scanButton.setOnClickListener {
            startScanning()
        }


    }

    override fun onStart() {
        super.onStart()
        startScanning()

    }

    private fun startScanning() {
        val newDialog: Dialog = Dialog(this)
        newDialog.setContentView(R.layout.popup_scannig)
        newDialog.show()
        bleManager.startScanning()
        val handler = Handler()
        handler.postDelayed({
            if (newDialog.isShowing) {
                newDialog.dismiss()
            }
        }, BleManager.SCANNING_PERIOD.toLong())
    }


}