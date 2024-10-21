package com.quantuityanalytics.quantuityanalytics.breaktest

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.google.android.material.button.MaterialButton
import com.quantuityanalytics.quantuityanalytics.MenuActivity
import com.quantuityanalytics.quantuityanalytics.R
import com.quantuityanalytics.quantuityanalytics.RealTimeTestActivity
import com.quantuityanalytics.quantuityanalytics.adapters.RecycleViewItemInterface
import com.quantuityanalytics.quantuityanalytics.ble.BleDeviceAdapter
import com.quantuityanalytics.quantuityanalytics.ble.BleDeviceManager
import com.quantuityanalytics.quantuityanalytics.ble.QABleDevice
import com.quantuityanalytics.quantuityanalytics.breaktest.BreakTestStepFragment.Companion
import com.quantuityanalytics.quantuityanalytics.utils.SharedPreferencesManager
import com.quantuityanalytics.quantuityanalytics.viewmodel.BreakViewModel

class BreakTestDevicesFragment:
    Fragment(R.layout.fragment_test_devices),
    RecycleViewItemInterface{

    companion object {
        const val TAG: String = "QuantuityAnalytics.BreakTestDevicesFragment"
        const val BLUETOOTH_ENABLE_CODE: Int = 3301
    }

    private var deviceAdapter: BleDeviceAdapter = BleDeviceAdapter(arrayListOf(), this)

    private var recyclerView: RecyclerView? = null
    private var animationView: LottieAnimationView? = null
    private var scanButton: MaterialButton? = null
    private var connectButton: MaterialButton? = null

    private val breakTestViewModel: BreakViewModel by activityViewModels()
    private var bleDeviceManager: BleDeviceManager? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        animationView = view.findViewById(R.id.animationView)
        recyclerView = view.findViewById(R.id.device_list)
        scanButton = view.findViewById(R.id.btn_scan)
        connectButton = view.findViewById(R.id.btn_connect_all)
        recyclerView?.layoutManager = LinearLayoutManager(context)
        recyclerView?.adapter = deviceAdapter

        scanButton?.setOnClickListener {
            bleDeviceManager?.startScanning()
        }

        connectButton?.setOnClickListener {
            breakTestViewModel.listOfDevices.value?.let { list -> bleDeviceManager?.connectTo(list) }
        }

        breakTestViewModel.listOfDevices.observe(viewLifecycleOwner, Observer { list ->
            Log.d(TAG, "Starting breakTestViewModel.listOfDevices")
            Log.d(TAG, "New array of devices ${list.size} detected")
            deviceAdapter.setDeviceList(list)
            deviceAdapter.notifyDataSetChanged()
            Log.d(TAG, "Ending breakTestViewModel.listOfDevices")
        })

        breakTestViewModel.scannerStatus.observe(viewLifecycleOwner, Observer { value ->
            if (value) {
                onScanningLayout()
            }else {
                if (deviceAdapter.itemCount == 0) {
                    onNoDeviceFoundLayout()
                } else {
                    onDeviceFoundLayout()
                }
            }
        })

        breakTestViewModel.startAction.observe(viewLifecycleOwner, Observer { value ->
            if (value) {
                scanButton?.visibility = View.INVISIBLE
                connectButton?.visibility = View.INVISIBLE
                deviceAdapter.isEnable = false
            }
        })

        breakTestViewModel.readAction.observe(viewLifecycleOwner, Observer { value->
            Log.d(TAG, "START READING ON DevicesFragment")
            breakTestViewModel.listOfDevices.value?.let { bleDeviceManager?.connectToRead(it) }
        })

    }

    private fun onScanningLayout() {
        scanButton?.isEnabled = false
        connectButton?.isEnabled = false
        animationView?.visibility = View.VISIBLE
        animationView?.setAnimation(R.raw.animation_bluetooth)
        animationView?.repeatMode = LottieDrawable.RESTART
        animationView?.repeatCount = 10
        animationView?.playAnimation()
        recyclerView?.visibility = View.INVISIBLE
    }

    private fun onNoDeviceFoundLayout() {
        scanButton?.isEnabled = true
        connectButton?.isEnabled = false
        recyclerView?.visibility = View.INVISIBLE
        animationView?.visibility = View.VISIBLE
        animationView?.setAnimation(R.raw.animation_no_device)
        animationView?.repeatMode = LottieDrawable.RESTART
        animationView?.repeatCount = 0
        animationView?.playAnimation()
    }

    private fun onDeviceFoundLayout() {
        scanButton?.isEnabled = true
        connectButton?.isEnabled = true
        animationView?.visibility = View.INVISIBLE
        recyclerView?.visibility = View.VISIBLE
    }

    override fun onStart() {
        super.onStart()
        val bluetoothManager = activity?.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter
        if (bluetoothAdapter != null) {
            if (!bluetoothAdapter.isEnabled) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    Log.d(RealTimeTestActivity.TAG, "Not enough permissions")
                    activity?.finish()
                    return
                }
                startActivityForResult(enableBtIntent, BLUETOOTH_ENABLE_CODE)
            } else {
                Log.d(RealTimeTestActivity.TAG, "On Start() init BleDeviceManager")
//                val list = arrayListOf<String>()
//                list.add("B4:3A:31:EF:52:8B")
//                list.add("B4:3A:31:EF:52:8C")
                val spm = SharedPreferencesManager(requireContext())
                val list = spm.getStringArrayList(SharedPreferencesManager.SP_GROUP_ADDRESS_KEY)
                bleDeviceManager = BleDeviceManager(requireContext(), bluetoothAdapter, list, breakTestViewModel)
                bleDeviceManager?.startScanning()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == BLUETOOTH_ENABLE_CODE) {
            if (resultCode == 0) {
                activity?.finish()
                val intent = Intent(activity, MenuActivity::class.java)
                startActivity(intent)
            }else {
                val bluetoothManager = activity?.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
                val bluetoothAdapter = bluetoothManager.adapter
//                val list = arrayListOf<String>()
//                list.add("B4:3A:31:EF:52:8B")
//                list.add("B4:3A:31:EF:52:8C")
                val spm = SharedPreferencesManager(requireContext())
                val list = spm.getStringArrayList(SharedPreferencesManager.SP_GROUP_ADDRESS_KEY)
                bleDeviceManager = BleDeviceManager(requireContext(),bluetoothAdapter, list ,breakTestViewModel)
                bleDeviceManager?.startScanning()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bleDeviceManager.let {
            Log.d(RealTimeTestActivity.TAG, "Disconnecting from all devices before destroy")
            bleDeviceManager?.disconnectFromAllDevices()
        }
    }

    override fun onDeviceClick(position: Int) {
        //deviceAdapter?.selectDevice(position)
        val actualDevice = deviceAdapter.getDeviceByPosition(position)
        if (actualDevice != null) {
            if (actualDevice.status < QABleDevice.STATUS_CONNECTED) {
                Log.d(TAG, "Changing status to STATUS_CONNECTING")
                //actualDevice.status = QABleDevice.STATUS_CONNECTING
                //breakTestViewModel.updateDevice(actualDevice)
                bleDeviceManager?.connectTo(arrayListOf(actualDevice))
            } else {
                Log.d(TAG, "Changing status to STATUS_DISCOVERED")
                bleDeviceManager?.disconnectFromDevices(arrayListOf(actualDevice))
            }
        }

    }
}