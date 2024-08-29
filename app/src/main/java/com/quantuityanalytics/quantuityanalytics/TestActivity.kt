package com.quantuityanalytics.quantuityanalytics

import android.bluetooth.BluetoothManager
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.quantuityanalytics.quantuityanalytics.adapters.RecycleViewItemInterface
import com.quantuityanalytics.quantuityanalytics.ble.BleDeviceAdapter
import com.quantuityanalytics.quantuityanalytics.ble.BleManager
import com.quantuityanalytics.quantuityanalytics.breaktest.BreakTestDevicesFragment
import com.quantuityanalytics.quantuityanalytics.breaktest.BreakTestScannerFragment
import com.quantuityanalytics.quantuityanalytics.breaktest.BreakTestStepFragment

class TestActivity: AppCompatActivity(), RecycleViewItemInterface {

    companion object {
        const val TAG: String = "QuantuityAnalytics.TestActivity"
    }

    private val fragmentScanner = BreakTestScannerFragment()
    private val fragmentDevices = BreakTestDevicesFragment()
    private val fragmentStep = BreakTestStepFragment()

    private val deviceAdapter = BleDeviceAdapter(context = this, dataSet = arrayListOf(), recycleViewItemInterface = this)
    private var bleManager: BleManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_break_test)

        // GET all devices for the test



        // GET all break test steps


        // BUILD an array/adapter


        // CREATE a result object


        // FOR EACH step load fragment


            // WAIT FOR RESULT


            // SAVE OR REPEAT


        // SEND RESULT TO RESULT ACTIVITY
    }

    private fun loadFragment(frame: Int, fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setReorderingAllowed(true)
            .replace(frame, fragment)
            .commit()
    }

    override fun onStart() {
        super.onStart()
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter
        if (bluetoothAdapter != null) {
            bleManager = BleManager(this,bluetoothAdapter, deviceAdapter)
        }
    }

    override fun onDeviceClick(position: Int) {
        TODO("Not yet implemented")
    }
}