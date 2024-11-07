package com.quantuityanalytics.quantuityanalytics.settings

import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.quantuityanalytics.quantuityanalytics.R
import com.quantuityanalytics.quantuityanalytics.utils.QAPreferencesKeys
import com.quantuityanalytics.quantuityanalytics.utils.QAPreferencesKeys.DEVICE_NAME
import com.quantuityanalytics.quantuityanalytics.utils.QAPreferencesKeys.SENSOR_CHARACTERISTIC_CALIBRATE
import com.quantuityanalytics.quantuityanalytics.utils.QAPreferencesKeys.SENSOR_CHARACTERISTIC_FIRMWARE
import com.quantuityanalytics.quantuityanalytics.utils.QAPreferencesKeys.SENSOR_CHARACTERISTIC_RESULT
import com.quantuityanalytics.quantuityanalytics.utils.QAPreferencesKeys.SENSOR_CHARACTERISTIC_WRITE
import com.quantuityanalytics.quantuityanalytics.utils.QAPreferencesKeys.SENSOR_SERVICE_CALIBRATE
import com.quantuityanalytics.quantuityanalytics.utils.QAPreferencesKeys.SENSOR_SERVICE_FIRMWARE
import com.quantuityanalytics.quantuityanalytics.utils.QAPreferencesKeys.SENSOR_SERVICE_RESULT
import com.quantuityanalytics.quantuityanalytics.utils.QAPreferencesManager
import com.quantuityanalytics.quantuityanalytics.utils.SharedPreferencesManager


class SettingsValuesFragment: Fragment(R.layout.fragment_settings_values) {

    companion object{
        const val TAG: String = "QuantuityAnalytics.SettingsValuesFragment"
    }

//    private var spm: SharedPreferencesManager? = null
    private lateinit var preferencesManager: QAPreferencesManager
    private lateinit var mainLayout: ConstraintLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainLayout = view.findViewById(R.id.main)

        val deviceName = view.findViewById<EditText>(R.id.device_name_value)
        val serviceResult = view.findViewById<EditText>(R.id.service_result)
        val characteristicWrite = view.findViewById<EditText>(R.id.characteristic_write)
        val characteristicResult = view.findViewById<EditText>(R.id.characteristic_result)
        val serviceFirmware = view.findViewById<EditText>(R.id.service_firmware)
        val serviceCalibrate = view.findViewById<EditText>(R.id.service_calibrate)
        val characteristicFirmware = view.findViewById<EditText>(R.id.characteristic_firmware)
        val characteristicCalibrate = view.findViewById<EditText>(R.id.characteristic_calibrate)

        val btnDeviceName = view.findViewById<ImageButton>(R.id.device_name_button)
        val btnServiceResult = view.findViewById<ImageButton>(R.id.service_result_btn)
        val btnServiceFirmware = view.findViewById<ImageButton>(R.id.service_firmware_btn)
        val btnServiceCalibrate = view.findViewById<ImageButton>(R.id.service_calibrate_btn)
        val btnCharacteristicResult = view.findViewById<ImageButton>(R.id.characteristic_result_btn)
        val btnCharacteristicWrite = view.findViewById<ImageButton>(R.id.characteristic_write_btn)
        val btnCharacteristicFirmware = view.findViewById<ImageButton>(R.id.characteristic_firmware_btn)
        val btnCharacteristicCalibrate = view.findViewById<ImageButton>(R.id.characteristic_calibrate_btn)

        deviceName.setText(preferencesManager.getString(DEVICE_NAME, ""))
        serviceResult.setText(preferencesManager.getString(SENSOR_SERVICE_RESULT, "dda4d145-fc52-4705-bb93-dd1f295aa522"))
        characteristicWrite.setText(preferencesManager.getString(SENSOR_CHARACTERISTIC_WRITE, "02AA6D7D-23B4-4C84-AF76-98A7699F7FE2"))
        characteristicResult.setText(preferencesManager.getString(SENSOR_CHARACTERISTIC_RESULT, "61a885a4-41c3-60d0-9a53-6d652a70d29c"))
        serviceFirmware.setText(preferencesManager.getString(SENSOR_SERVICE_FIRMWARE, "dda4d145-fc52-4705-bb93-dd1f295aa522"))
        serviceCalibrate.setText(preferencesManager.getString(SENSOR_SERVICE_CALIBRATE, "dda4d145-fc52-4705-bb93-dd1f295aa522"))
        characteristicFirmware.setText(preferencesManager.getString(SENSOR_CHARACTERISTIC_FIRMWARE))
        characteristicCalibrate.setText(preferencesManager.getString(SENSOR_CHARACTERISTIC_CALIBRATE))

        btnDeviceName.setOnClickListener {
            onSettingButton(btnDeviceName, deviceName, DEVICE_NAME)
        }

        btnServiceResult.setOnClickListener {
            onSettingButton(btnServiceResult, serviceResult, SENSOR_SERVICE_RESULT)
        }

        btnServiceFirmware.setOnClickListener {
            onSettingButton(btnServiceFirmware, serviceFirmware, SENSOR_SERVICE_FIRMWARE)
        }

        btnServiceCalibrate.setOnClickListener {
            onSettingButton(btnServiceCalibrate, serviceCalibrate, SENSOR_SERVICE_CALIBRATE)
        }

        btnCharacteristicResult.setOnClickListener {
            onSettingButton(btnCharacteristicResult, characteristicResult, SENSOR_CHARACTERISTIC_RESULT)
        }

        btnCharacteristicWrite.setOnClickListener {
            onSettingButton(btnCharacteristicWrite, characteristicWrite, SENSOR_CHARACTERISTIC_WRITE)
        }

        btnCharacteristicFirmware.setOnClickListener {
            onSettingButton(btnCharacteristicFirmware, characteristicFirmware, SENSOR_CHARACTERISTIC_FIRMWARE)
        }

        btnCharacteristicCalibrate.setOnClickListener {
            onSettingButton(btnCharacteristicCalibrate, characteristicCalibrate, SENSOR_CHARACTERISTIC_CALIBRATE)
        }


//        val mlModel = view.findViewById<Spinner>(R.id.device_model_value)
//        val mlOptions = resources.getStringArray(R.array.ml_models)
//        val mlIndex = mlOptions.indexOf(spm?.getString(SharedPreferencesManager.SP_DEVICE_ML_MODEL))

//        deviceName.setText(spm?.getString(SharedPreferencesManager.SP_DEVICE_NAME_KEY), TextView.BufferType.EDITABLE)

//        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, mlOptions)
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        mlModel.adapter = adapter
//        mlModel.setSelection(mlIndex)
//
//        mlModel.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
//                val selectedItem = mlOptions[position]
//                spm?.saveString(selectedItem, SharedPreferencesManager.SP_DEVICE_ML_MODEL)
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>) {
//                Log.d(TAG, "No ML Model was selected")
//            }
//        }

//        btnDeviceName.setOnClickListener {
//            if (!deviceName.isEnabled) {
//                deviceName.isEnabled = true
//                btnDeviceName.setImageResource(R.drawable.baseline_save_24)
//            } else {
//                if (deviceName.text.isNotEmpty()) {
//                    preferencesManager.putString(QAPreferencesKeys.DEVICE_NAME, deviceName.text.toString())
//                    showSnackBar(mainLayout, "Value was saved successfully")
//                    deviceName.isEnabled = false
//                    btnDeviceName.setImageResource(R.drawable.baseline_edit_24)
//                }
//            }
//        }
//
//        btnServiceResult.setOnClickListener {
//            if (!serviceResult.isEnabled) {
//                serviceResult.isEnabled = true
//                btnServiceResult.setImageResource(R.drawable.baseline_save_24)
//            } else {
//                if (serviceResult.text.isNotEmpty()) {
//                    preferencesManager.putString(SENSOR_SERVICE_RESULT, serviceResult.text.toString())
//                    showSnackBar(mainLayout, "Value was saved successfully")
//                    serviceResult.isEnabled = false
//                    btnServiceResult.setImageResource(R.drawable.baseline_edit_24)
//                }
//            }
//        }
//
//        btnServiceFirmware.setOnClickListener {
//            if (!serviceFirmware.isEnabled) {
//                serviceFirmware.isEnabled = true
//                btnServiceFirmware.setImageResource(R.drawable.baseline_save_24)
//            } else {
//                if (serviceFirmware.text.isNotEmpty()) {
//                    preferencesManager.putString(SENSOR_SERVICE_FIRMWARE, serviceFirmware.text.toString())
//                    showSnackBar(mainLayout, "Value was saved successfully")
//                    serviceFirmware.isEnabled = false
//                    btnServiceFirmware.setImageResource(R.drawable.baseline_edit_24)
//                }
//            }
//        }

    }

    private fun onSettingButton(button: ImageButton, text: EditText, key: String) {
        if (!text.isEnabled) {
            text.isEnabled = true
            button.setImageResource(R.drawable.baseline_save_24)
        } else {
            if (text.text.isNotEmpty()) {
                preferencesManager.putString(key, text.text.toString())
                showSnackBar(mainLayout, "Value was saved successfully")
                text.isEnabled = false
                button.setImageResource(R.drawable.baseline_edit_24)
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        preferencesManager = QAPreferencesManager(context)
    }

    private fun showSnackBar(mainLayout: ConstraintLayout, msg: String) {
        val snack: Snackbar = Snackbar.make(mainLayout, msg, Snackbar.LENGTH_LONG)
            .setBackgroundTint(resources.getColor(R.color.primary_light))
            .setTextColor(resources.getColor(R.color.white))
        val view = snack.view
        val params = view.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        params.width = FrameLayout.LayoutParams.FILL_PARENT
        view.layoutParams = params
        snack.show()
    }
}