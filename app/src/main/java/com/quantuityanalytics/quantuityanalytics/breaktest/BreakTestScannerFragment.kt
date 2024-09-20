package com.quantuityanalytics.quantuityanalytics.breaktest

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.google.android.material.button.MaterialButton
import com.quantuityanalytics.quantuityanalytics.R
import com.quantuityanalytics.quantuityanalytics.ble.QABleDevice
import com.quantuityanalytics.quantuityanalytics.viewmodel.BreakViewModel

class BreakTestScannerFragment: Fragment(R.layout.fragment_test_scanner) {

    companion object{
        const val TAG: String = "QuantuityAnalytics.BreakTestScannerFragment"
    }

    private val breakTestViewModel: BreakViewModel by activityViewModels()

    //private var animationView: LottieAnimationView? = null
    //private var commentText: TextView? = null
    //private var startButton: MaterialButton? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        animationView = view.findViewById(R.id.animationView)
//        commentText = view.findViewById(R.id.comment)
        val startButton = view.findViewById<MaterialButton>(R.id.btn_start)

        //startButton = view.findViewById(R.id.btn_start)

        startButton?.setOnClickListener {
            breakTestViewModel.setStartAction(true)
        }

        breakTestViewModel.listOfDevices.observe(viewLifecycleOwner, Observer { list ->
            var hasDevice = false
            for (device in list) {
                if (device.status >= QABleDevice.STATUS_CONNECTED) {
                    hasDevice = true
                }
            }
            if (hasDevice) {
                startButton.isEnabled = true
            }
        })


//        breakTestViewModel.scannerStatus.observe(viewLifecycleOwner, Observer { value ->
//            if (value) {
//                onScanning()
//            } else {
//                onWaiting()
//            }
//        })
    }

    override fun onDestroy() {
        super.onDestroy()
    }

//    fun onScanning() {
//        animationView?.visibility = View.VISIBLE
//        animationView?.repeatMode = LottieDrawable.RESTART
//        animationView?.repeatCount = 8
//        animationView?.playAnimation()
//        scanButton?.visibility = View.INVISIBLE
//        commentText?.text = resources.getString(R.string.break_test_scanning)
//    }
//
//    fun onWaiting() {
//        animationView?.visibility = View.INVISIBLE
//        scanButton?.visibility = View.VISIBLE
//        commentText?.text = resources.getString(R.string.break_test_start)
//    }
}