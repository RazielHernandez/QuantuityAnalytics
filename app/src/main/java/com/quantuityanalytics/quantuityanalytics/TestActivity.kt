package com.quantuityanalytics.quantuityanalytics


import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.quantuityanalytics.quantuityanalytics.ble.BleDeviceAdapter
import com.quantuityanalytics.quantuityanalytics.ble.BleDeviceInterface
import com.quantuityanalytics.quantuityanalytics.ble.BleManager


class TestActivity: AppCompatActivity(), BleDeviceInterface {

    companion object {
        const val TAG: String = "QuantuityAnalytics.TestActivity"
    }

    private val deviceAdapter = BleDeviceAdapter(context = this, dataSet = arrayListOf(), deviceInterface = this)
    private val bleManager: BleManager = BleManager(this, deviceAdapter)

    private var layoutScanning: LinearLayout? = null
    private var layoutDevices: LinearLayout? = null
    private var scanButton: MaterialButton? = null
    private var layoutMain: ConstraintLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_brake_test)

        layoutScanning = findViewById(R.id.scanning_logo)
        layoutDevices = findViewById(R.id.scanning_devices)
        layoutMain = findViewById(R.id.main)
        scanButton = findViewById(R.id.btn_scan)

        val recyclerView: RecyclerView = findViewById(R.id.device_list)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = deviceAdapter

        val scanButton: MaterialButton = findViewById(R.id.btn_scan)
        scanButton.setOnClickListener {
            startScanning()
        }

    }

    private fun startScanning() {
        Log.d(TAG, "start scanning")
        layoutScanning?.visibility = View.VISIBLE
        layoutDevices?.visibility = View.INVISIBLE
        //scanButton?.text = "Stop"

        bleManager.startScanning()
        val handler = Handler()
        handler.postDelayed({
            layoutScanning?.visibility = View.INVISIBLE
            layoutDevices!!.visibility = View.VISIBLE
            //scanButton?.text = "Start"

            if (deviceAdapter.itemCount <= 0){
                showMessageBar("No device was founded")
            }
        }, BleManager.SCANNING_PERIOD.toLong())
    }

    override fun onStart() {
        super.onStart()
        startScanning()
    }

    private fun loadFragment(frame: Int, fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setReorderingAllowed(true)
            .replace(frame, fragment)
            .commit()
    }

    private fun showMessageBar(msg: String) {
        val snack: Snackbar = Snackbar.make(layoutMain!!, msg, Snackbar.LENGTH_LONG)
            .setBackgroundTint(resources.getColor(R.color.primary_light))
            .setTextColor(resources.getColor(R.color.white))

        val view = snack.view

        val params = view.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        params.width = FrameLayout.LayoutParams.FILL_PARENT
        view.layoutParams = params
        snack.show()

    }

    override fun onDeviceClick(position: Int) {
        deviceAdapter.getDeviceByPosition(position)?.let { bleManager.connectToDevice(it) }
    }


}