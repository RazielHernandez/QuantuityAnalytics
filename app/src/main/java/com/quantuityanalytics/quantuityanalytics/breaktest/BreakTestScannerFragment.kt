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
import com.quantuityanalytics.quantuityanalytics.viewmodel.BreakViewModel

class BreakTestScannerFragment: Fragment(R.layout.fragment_test_scanner) {

    companion object{
        const val TAG: String = "QuantuityAnalytics.BreakTestScannerFragment"
    }

    private val breakTestViewModel: BreakViewModel by activityViewModels()

    private var animationView: LottieAnimationView? = null
    private var commentText: TextView? = null
    private var scanButton: MaterialButton? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        animationView = view.findViewById(R.id.animationView)
        commentText = view.findViewById(R.id.comment)
        scanButton = view.findViewById(R.id.btn_scan)

        val scanButton: MaterialButton = view.findViewById(R.id.btn_scan)

        scanButton.setOnClickListener {
            breakTestViewModel.setScannerStatus(true)
        }

        breakTestViewModel.scannerStatus.observe(viewLifecycleOwner, Observer { value ->
            if (value) {
                onScanning()
            } else {
                onWaiting()
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun onScanning() {
        animationView?.visibility = View.VISIBLE
        animationView?.repeatMode = LottieDrawable.RESTART
        animationView?.repeatCount = 8
        animationView?.playAnimation()
        scanButton?.visibility = View.INVISIBLE
        commentText?.text = resources.getString(R.string.break_test_scanning)
    }

    fun onWaiting() {
        animationView?.visibility = View.INVISIBLE
        scanButton?.visibility = View.VISIBLE
        commentText?.text = resources.getString(R.string.break_test_start)
    }
}