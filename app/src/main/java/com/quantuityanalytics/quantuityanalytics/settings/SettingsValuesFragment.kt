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
import com.quantuityanalytics.quantuityanalytics.utils.SharedPreferencesManager


class SettingsValuesFragment: Fragment(R.layout.fragment_settings_values) {

    companion object{
        const val TAG: String = "QuantuityAnalytics.SettingsValuesFragment"
    }

    private var spm: SharedPreferencesManager? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val deviceNameView = view.findViewById<EditText>(R.id.device_name_value)
        val deviceNameButton = view.findViewById<ImageButton>(R.id.device_name_button)
        val mainLayout = view.findViewById<ConstraintLayout>(R.id.main)
        val mlModel = view.findViewById<Spinner>(R.id.device_model_value)
        val mlOptions = resources.getStringArray(R.array.ml_models)
        val mlIndex = mlOptions.indexOf(spm?.getString(SharedPreferencesManager.SP_DEVICE_ML_MODEL))

        deviceNameView.setText(spm?.getString(SharedPreferencesManager.SP_DEVICE_NAME_KEY), TextView.BufferType.EDITABLE)

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, mlOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mlModel.adapter = adapter
        mlModel.setSelection(mlIndex)

        mlModel.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = mlOptions[position]
                spm?.saveString(selectedItem, SharedPreferencesManager.SP_DEVICE_ML_MODEL)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                Log.d(TAG, "No ML Model was selected")
            }
        }

        deviceNameButton.setOnClickListener {
            if (!deviceNameView.isEnabled) {
                deviceNameView.isEnabled = true
                deviceNameButton.setImageResource(R.drawable.baseline_save_24)
            } else {
                if (deviceNameView.text.isNotEmpty()) {
                    spm?.saveString(deviceNameView.text.toString(), SharedPreferencesManager.SP_DEVICE_NAME_KEY)
                    showSnackBar(mainLayout, "Value was saved successfully")
                    deviceNameView.isEnabled = false
                    deviceNameButton.setImageResource(R.drawable.baseline_edit_24)
                }
            }
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        spm = SharedPreferencesManager(context)
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