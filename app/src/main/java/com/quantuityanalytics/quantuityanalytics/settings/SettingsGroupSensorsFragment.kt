package com.quantuityanalytics.quantuityanalytics.settings

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quantuityanalytics.quantuityanalytics.R
import com.quantuityanalytics.quantuityanalytics.adapters.GroupSensorAdapter
import com.quantuityanalytics.quantuityanalytics.adapters.RecycleViewItemInterface
import com.quantuityanalytics.quantuityanalytics.model.SensorGroup
import com.quantuityanalytics.quantuityanalytics.utils.SharedPreferencesManager
import com.quantuityanalytics.quantuityanalytics.viewmodel.SensorViewModel

class SettingsGroupSensorsFragment: Fragment(R.layout.fragment_sensors_groups), RecycleViewItemInterface {

    private val sensorViewModel: SensorViewModel by viewModels({requireParentFragment()})

    private var spm: SharedPreferencesManager? = null
    private var groupAdapter: GroupSensorAdapter? = null

    companion object{
        const val TAG = "QuantuityAnalytics.SettingsGroupSensorsFragment"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val mListView = view.findViewById<RecyclerView>(R.id.recycleView)
        mListView?.layoutManager = LinearLayoutManager(context)
        mListView?.adapter = groupAdapter
        mListView?.addItemDecoration(
            DividerItemDecoration(
                activity,
                LinearLayoutManager(context).orientation
            )
        )

        val addGroupText = view.findViewById<EditText>(R.id.edit_text)
        val addGroupButton = view.findViewById<ImageButton>(R.id.newGroupButton)
        addGroupButton.setOnClickListener {
            val newGroupName = view.findViewById<EditText>(R.id.edit_text)
            if (newGroupName.text.isNotEmpty()) {
                val newList = groupAdapter?.addItem(SensorGroup(name = newGroupName.text.toString(), isSelected = false,  listOfAddresses =  arrayListOf()) )
                if (newList != null){
                    spm?.saveGroupArrayList(newList, SharedPreferencesManager.SP_GROUP_ADDRESS_KEY)
                }
                addGroupText.text.clear()
                addGroupText.clearFocus()

                val array = spm!!.getGroupArrayList(SharedPreferencesManager.SP_GROUP_ADDRESS_KEY)
                groupAdapter?.setDataSet(array)
            }
        }

        sensorViewModel.closeAction.observe(viewLifecycleOwner, Observer {
            val array = spm!!.getGroupArrayList(SharedPreferencesManager.SP_GROUP_ADDRESS_KEY)
            groupAdapter?.setDataSet(array)
        })

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        spm = SharedPreferencesManager(context)
        groupAdapter = GroupSensorAdapter(context, spm!!.getGroupArrayList(SharedPreferencesManager.SP_GROUP_ADDRESS_KEY), this)
    }

    override fun onDeviceClick(position: Int) {
        val groupSelected = groupAdapter?.getItemAt(position)
        if (groupSelected != null){
            sensorViewModel.setGroup(groupSelected)
        }
    }

    override fun onDeviceSelected(position: Int, isChecked: Boolean) {
        val groupSelected = groupAdapter?.getItemAt(position)
        if (groupSelected != null) {
            groupSelected.isSelected = isChecked
            spm?.updateGroup(groupSelected, SharedPreferencesManager.SP_GROUP_ADDRESS_KEY)
        }
        Log.d(TAG, "Device Selected $position is ${groupSelected?.name} and is selected: $isChecked")
    }

}