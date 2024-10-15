package com.quantuityanalytics.quantuityanalytics.settings

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.quantuityanalytics.quantuityanalytics.R
import com.quantuityanalytics.quantuityanalytics.adapters.AddressAdapter
import com.quantuityanalytics.quantuityanalytics.adapters.GroupSensorAdapter
import com.quantuityanalytics.quantuityanalytics.adapters.RecycleViewItemInterface
import com.quantuityanalytics.quantuityanalytics.model.SensorGroup
import com.quantuityanalytics.quantuityanalytics.utils.SharedPreferencesManager
import com.quantuityanalytics.quantuityanalytics.viewmodel.SensorViewModel

class SettingsAddressesSensorsFragment: Fragment(R.layout.fragment_sensors_address), RecycleViewItemInterface {

    private val sensorViewModel: SensorViewModel by viewModels({requireParentFragment()})

    private var spm: SharedPreferencesManager? = null
    private var addressAdapter: AddressAdapter? = null

    private var actualGroup = SensorGroup(name = "No name", isSelected = false, listOfAddresses =  arrayListOf())

    companion object {
        const val TAG = "QuantuityAnalytics.SettingsAddressesSensorsFragment"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val mListView = view.findViewById<RecyclerView>(R.id.recycleView)
        mListView?.layoutManager = LinearLayoutManager(context)
        mListView?.adapter = addressAdapter
        mListView?.addItemDecoration(
            DividerItemDecoration(
                activity,
                LinearLayoutManager(context).orientation
            )
        )

        view.findViewById<ImageButton>(R.id.btn_delete).setOnClickListener {
            spm?.deleteGroup(actualGroup, SharedPreferencesManager.SP_GROUP_ADDRESS_KEY)
            sensorViewModel.setCloseAction(true)
        }

        view.findViewById<ImageButton>(R.id.btn_edit).setOnClickListener {
            showEditDialog()
        }

        view.findViewById<FloatingActionButton>(R.id.floatingButton).setOnClickListener {
            showInputDialog()
        }

        sensorViewModel.group.observe(viewLifecycleOwner, Observer { it ->
            actualGroup = it
            view.findViewById<TextView>(R.id.group_name).text = actualGroup.name
            val addresses = addressAdapter?.setDataSet(actualGroup.listOfAddresses)

        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        spm = SharedPreferencesManager(context)
        addressAdapter = AddressAdapter(context, arrayListOf(), this)
    }

    override fun onDeviceClick(position: Int) {
        actualGroup.listOfAddresses = addressAdapter!!.deleteItemAt(position)
        spm?.updateGroup(actualGroup, SharedPreferencesManager.SP_GROUP_ADDRESS_KEY)

    }

    private fun showEditDialog() {
        val inputEditText = EditText(view?.context)
        inputEditText.setText(actualGroup.name)

        val dialog = AlertDialog.Builder(view?.context)
            .setTitle("Group name")
            .setMessage("Enter the new group name:")
            .setView(inputEditText)
            .setPositiveButton("OK") { _, _ ->
                val inputText = inputEditText.text.toString()
                if (inputText.isNotBlank()) {
                    actualGroup.name = inputText
                    spm?.updateGroup(actualGroup, SharedPreferencesManager.SP_GROUP_ADDRESS_KEY)
                    sensorViewModel.setCloseAction(true)
                } else {
                    Toast.makeText(view?.context, "Group name can´t be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        // Show the dialog
        dialog.show()
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
                    actualGroup.listOfAddresses = addressAdapter!!.addItem(inputText)
                    spm?.updateGroup(actualGroup, SharedPreferencesManager.SP_GROUP_ADDRESS_KEY)
                } else {
                    Toast.makeText(view?.context, "Text can´t be empty", Toast.LENGTH_SHORT).show()
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