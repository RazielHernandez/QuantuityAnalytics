package com.quantuityanalytics.quantuityanalytics


import android.bluetooth.BluetoothManager
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
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
    private var bleManager: BleManager? = null

    private var layoutMain: ConstraintLayout? = null
    private var animationView: LottieAnimationView? = null
    private var recyclerView: RecyclerView? = null
    private var commentText: TextView? = null
    private var scanButton: MaterialButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_brake_test)

        layoutMain = findViewById(R.id.main)
        scanButton = findViewById(R.id.btn_scan)
        animationView = findViewById(R.id.animationView)
        recyclerView = findViewById(R.id.device_list)
        commentText = findViewById(R.id.comment)

        recyclerView?.layoutManager = LinearLayoutManager(this)
        recyclerView?.adapter = deviceAdapter

        scanButton?.setOnClickListener {
            startScanning()
        }


    }

    private fun startScanning() {
        if (bleManager != null) {
            Log.d(TAG, "start scanning")
            layoutScanning()

            bleManager?.startScanning()
            val handler = Handler()
            handler.postDelayed({

                if (deviceAdapter.itemCount <= 0){
                    layoutNoDevice()
                    showMessageBar("No device was founded")
                } else {
                    layoutListOfDevice()
                }
            }, BleManager.SCANNING_PERIOD.toLong())
        } else {
            showMessageBar("No Bluetooth adapter was founded")
        }
    }

    override fun onStart() {
        super.onStart()
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter
        if (bluetoothAdapter != null) {
            bleManager = BleManager(this,bluetoothAdapter, deviceAdapter)
        }
        startScanning()
    }

    private fun layoutScanning() {
        animationView?.visibility = View.VISIBLE
        animationView?.setAnimation(R.raw.animation_bluetooth)
        animationView?.repeatMode = LottieDrawable.RESTART
        animationView?.repeatCount = 8
        animationView?.playAnimation()
        recyclerView?.visibility = View.INVISIBLE
        scanButton?.visibility = View.INVISIBLE
        commentText?.visibility = View.VISIBLE
        commentText?.text = resources.getString(R.string.break_test_scanning)
    }

    private fun layoutNoDevice() {
        animationView?.visibility = View.VISIBLE
        animationView?.setAnimation(R.raw.animation_no_device)
        animationView?.repeatMode = LottieDrawable.RESTART
        animationView?.repeatCount = 0
        animationView?.playAnimation()
        recyclerView?.visibility = View.INVISIBLE
        commentText?.visibility = View.VISIBLE
        commentText?.text = resources.getString(R.string.empty_device_list)
        scanButton?.visibility = View.VISIBLE
    }

    private fun layoutListOfDevice() {
        animationView?.visibility = View.INVISIBLE
        recyclerView?.visibility = View.VISIBLE
        commentText?.visibility = View.INVISIBLE
        scanButton?.visibility = View.VISIBLE
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
        deviceAdapter.getDeviceByPosition(position)?.let { bleManager?.connectToDevice(it) }
    }

}