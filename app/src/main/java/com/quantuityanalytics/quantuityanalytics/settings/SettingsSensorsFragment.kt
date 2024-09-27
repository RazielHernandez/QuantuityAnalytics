package com.quantuityanalytics.quantuityanalytics.settings

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.quantuityanalytics.quantuityanalytics.R
import com.quantuityanalytics.quantuityanalytics.adapters.AddressAdapter
import com.quantuityanalytics.quantuityanalytics.adapters.RecycleViewItemInterface
import com.quantuityanalytics.quantuityanalytics.model.SensorMacAddress
import com.quantuityanalytics.quantuityanalytics.utils.SharedPreferencesManager

class SettingsSensorsFragment: Fragment(R.layout.fragment_settings_sensors), RecycleViewItemInterface {

    private var spm: SharedPreferencesManager? = null
    private var addressAdapter: AddressAdapter? = null

    companion object{
        const val TAG = "QuantuityAnalytics.SettingsSensorsFragment"

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mListView = view.findViewById<RecyclerView>(R.id.listDevice)
        mListView?.layoutManager = LinearLayoutManager(context)
        mListView?.adapter = addressAdapter
        mListView?.addItemDecoration(
            DividerItemDecoration(
                activity,
                LinearLayoutManager(context).orientation
            )
        )

        val fab = view.findViewById<FloatingActionButton>(R.id.floatingButton)
        fab.setOnClickListener {
            showInputDialog()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        spm = SharedPreferencesManager(context)
        addressAdapter = AddressAdapter(context, spm!!.getArrayList(SharedPreferencesManager.SP_ADDRESSES_KEY), this)

    }

    override fun onDeviceClick(position: Int) {
        Log.d(TAG, "Delete item on position $position")
        val newAddressList = addressAdapter?.deleteItemAt(position)
        if (newAddressList != null) {
            spm?.saveArrayList(newAddressList, SharedPreferencesManager.SP_ADDRESSES_KEY)
        }
    }

    private fun showInputDialog() {
        val inputEditText = EditText(view?.context)
        inputEditText.hint = "MAC Address"

        val dialog = AlertDialog.Builder(view?.context)
            .setTitle("MAC Address")
            .setMessage("Please enter the new Mac Address:")
            .setView(inputEditText)
            .setPositiveButton("OK") { _, _ ->
                val inputText = inputEditText.text.toString()
                if (inputText.isNotBlank()) {
                    val newListDataSet = addressAdapter?.addItem(SensorMacAddress((inputText)))
                    if (newListDataSet != null) {
                        spm?.saveArrayList(newListDataSet, SharedPreferencesManager.SP_ADDRESSES_KEY)
                    }
                } else {
                    Toast.makeText(view?.context, "Input is empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        // Show the dialog
        dialog.show()
    }

}