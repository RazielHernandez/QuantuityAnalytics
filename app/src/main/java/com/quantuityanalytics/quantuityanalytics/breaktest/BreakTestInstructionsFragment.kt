package com.quantuityanalytics.quantuityanalytics.breaktest

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.android.material.button.MaterialButton
import com.quantuityanalytics.quantuityanalytics.R
import com.quantuityanalytics.quantuityanalytics.ble.QABleDevice
import com.quantuityanalytics.quantuityanalytics.viewmodel.BreakViewModel

class BreakTestInstructionsFragment: Fragment(R.layout.fragment_test_scanner) {

    companion object{
        const val TAG: String = "QuantuityAnalytics.BreakTestScannerFragment"
    }

    private val breakTestViewModel: BreakViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val startButton = view.findViewById<MaterialButton>(R.id.btn_start)
        val closeButton = view.findViewById<Button>(R.id.btn_close)

        startButton?.setOnClickListener {
            breakTestViewModel.setStartAction(true)
        }

        closeButton.setOnClickListener {
            activity?.finish()
        }

        breakTestViewModel.listOfDevices.observe(viewLifecycleOwner, Observer { list ->
            var hasDevice = false
            for (device in list) {
                if (device.status >= QABleDevice.STATUS_CONNECTED) {
                    hasDevice = true
                }
            }

            startButton.isEnabled = hasDevice
            /*if (hasDevice) {
                startButton.isEnabled = true
            } else {
                startButton.isEnabled = false
            }*/
        })
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}