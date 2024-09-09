package com.quantuityanalytics.quantuityanalytics.breaktest

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.button.MaterialButton
import com.quantuityanalytics.quantuityanalytics.R
import com.quantuityanalytics.quantuityanalytics.adapters.RecycleViewItemInterface
import com.quantuityanalytics.quantuityanalytics.ble.BleDeviceAdapter
import com.quantuityanalytics.quantuityanalytics.model.TestStep
import com.quantuityanalytics.quantuityanalytics.viewmodel.BreakViewModel

class BreakTestDevicesFragment:
    Fragment(R.layout.fragment_test_devices),
    RecycleViewItemInterface{

    companion object {
        const val TAG: String = "QuantuityAnalytics.BreakTestDevicesFragment"
    }

    private var deviceAdapter: BleDeviceAdapter? = null

    private var recyclerView: RecyclerView? = null
    private var animationView: LottieAnimationView? = null

    private val breakTestViewModel: BreakViewModel by activityViewModels()


    override fun onAttach(context: Context) {
        super.onAttach(context)
        deviceAdapter = BleDeviceAdapter(arrayListOf(), this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val startButton: MaterialButton = view.findViewById(R.id.btn_start)
        val closeButton: MaterialButton = view.findViewById(R.id.btn_close)
        animationView = view.findViewById(R.id.animationView)
        recyclerView = view.findViewById(R.id.device_list)
        recyclerView?.layoutManager = LinearLayoutManager(context)
        recyclerView?.adapter = deviceAdapter

        closeButton.setOnClickListener {
            activity?.finish()
        }

        startButton.setOnClickListener {
            breakTestViewModel.setStartAction(true)
        }

        breakTestViewModel.listOfDevices.observe(viewLifecycleOwner, Observer { list ->
            Log.d(TAG, "New array of devices detected")
            deviceAdapter?.setDeviceList(list)
            deviceAdapter?.notifyDataSetChanged()

        })

        breakTestViewModel.scannerStatus.observe(viewLifecycleOwner, Observer { value ->
            // IF is scanning
            if (value) {
                startButton.isEnabled = false
                closeButton.isEnabled = false
                animationView?.visibility = View.INVISIBLE
                recyclerView?.visibility = View.VISIBLE
            }else {
                // IF is not scanning
                closeButton.isEnabled = true
                // There are no items to show
                if (deviceAdapter != null && deviceAdapter?.itemCount == 0) {
                    recyclerView?.visibility = View.INVISIBLE
                    animationView?.visibility = View.VISIBLE
                    animationView?.playAnimation()
                    startButton.isEnabled = true
                } else {
                    // Scanner return at least one device
                    animationView?.visibility = View.INVISIBLE
                    recyclerView?.visibility = View.VISIBLE
                    startButton.isEnabled = true
                }
            }
        })

        breakTestViewModel.startAction.observe(viewLifecycleOwner, Observer { value ->
            if (value) {
                startButton.visibility = View.INVISIBLE
                closeButton.visibility = View.INVISIBLE
            }
        })

    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onDeviceClick(position: Int) {
        deviceAdapter?.selectDevice(position)
    }
}