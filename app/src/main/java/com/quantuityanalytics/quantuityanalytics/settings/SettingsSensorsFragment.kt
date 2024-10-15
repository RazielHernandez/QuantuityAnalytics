package com.quantuityanalytics.quantuityanalytics.settings

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.quantuityanalytics.quantuityanalytics.R
import com.quantuityanalytics.quantuityanalytics.adapters.RecycleViewItemInterface
import com.quantuityanalytics.quantuityanalytics.viewmodel.SensorViewModel

class SettingsSensorsFragment: Fragment(R.layout.fragment_settings_sensors) {

    private val viewModel: SensorViewModel by viewModels()

    private var groupSensorsFragment: SettingsGroupSensorsFragment = SettingsGroupSensorsFragment()
    private var addressSensorFragment: SettingsAddressesSensorsFragment = SettingsAddressesSensorsFragment()

    companion object{
        const val TAG = "QuantuityAnalytics.SettingsSensorsFragment"

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.group.observe(viewLifecycleOwner, Observer { it ->
            if (it != null) {
                loadFragmentInChild(R.id.sensor_elements_fragment, addressSensorFragment)
            }
        })

        viewModel.closeAction.observe(viewLifecycleOwner, Observer {
            removeFragmentInChild(addressSensorFragment)
        })


    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        loadFragmentInChild(R.id.sensor_group_fragment, groupSensorsFragment)
    }

    private fun loadFragmentInChild(frame: Int, fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(frame, fragment)
            .commit()
    }

    private fun removeFragmentInChild(fragment: Fragment) {
//        val childFragment = parentFragmentManager.findFragmentByTag(fragmentTag)
//        childFragment?.let {
//            parentFragmentManager.beginTransaction()
//                .remove(it)
//                .commit()
//        }
        childFragmentManager.beginTransaction()
            .remove(fragment)
            .commit()
    }

}