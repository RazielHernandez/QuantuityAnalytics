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
import com.quantuityanalytics.quantuityanalytics.utils.QAPreferencesConverter
import com.quantuityanalytics.quantuityanalytics.utils.QAPreferencesKeys
import com.quantuityanalytics.quantuityanalytics.utils.QAPreferencesManager
import com.quantuityanalytics.quantuityanalytics.viewmodel.SensorViewModel

class SettingsGroupSensorsFragment: Fragment(R.layout.fragment_sensors_groups), RecycleViewItemInterface {

    private val sensorViewModel: SensorViewModel by viewModels({requireParentFragment()})

    //private var spm: SharedPreferencesManager? = null
    private lateinit var groupAdapter: GroupSensorAdapter
    private lateinit var preferencesManager: QAPreferencesManager

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
            //val newGroupName = view.findViewById<EditText>(R.id.edit_text)
            if (addGroupText.text.isNotEmpty()) {
                val newList = groupAdapter.addItem(SensorGroup(name = addGroupText.text.toString(), isSelected = false,  listOfAddresses =  arrayListOf()) )
                val string = QAPreferencesConverter.convertGroupListToString(newList)
                preferencesManager.putString(QAPreferencesKeys.SENSOR_LIST, string)

//                if (newList != null){
//                    val string = QAPreferencesConverter.convertGroupListToString(newList)
//                    preferencesManager.putString(QAPreferencesKeys.SENSOR_LIST, string)
//                    spm?.saveGroupArrayList(newList, SharedPreferencesManager.SP_GROUP_ADDRESS_KEY)
//                }

                addGroupText.text.clear()
                addGroupText.clearFocus()

                //val array = spm!!.getGroupArrayList(SharedPreferencesManager.SP_GROUP_ADDRESS_KEY)
                val json =  preferencesManager.getString(QAPreferencesKeys.SENSOR_LIST)
                val listOfSensorGroup = QAPreferencesConverter.convertStringToGroupList(json)
                groupAdapter.setDataSet(listOfSensorGroup)
            }
        }

        sensorViewModel.closeAction.observe(viewLifecycleOwner, Observer {
            //val array = spm!!.getGroupArrayList(SharedPreferencesManager.SP_GROUP_ADDRESS_KEY)
            val json =  preferencesManager.getString(QAPreferencesKeys.SENSOR_LIST)
            val listOfSensorGroup = QAPreferencesConverter.convertStringToGroupList(json)
            groupAdapter.setDataSet(listOfSensorGroup)
        })

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        //spm = SharedPreferencesManager(context)
        preferencesManager = QAPreferencesManager(context)
        val json =  preferencesManager.getString(QAPreferencesKeys.SENSOR_LIST)
        val listOfSensorGroup = QAPreferencesConverter.convertStringToGroupList(json)
        groupAdapter = GroupSensorAdapter(context, listOfSensorGroup, this)
    }

    override fun onDeviceClick(position: Int) {
        val groupSelected = groupAdapter.getItemAt(position)
        sensorViewModel.setGroup(groupSelected)

    }

    override fun onDeviceSelected(position: Int, isChecked: Boolean) {
        val groupSelected = groupAdapter.getItemAt(position)
        val json =  preferencesManager.getString(QAPreferencesKeys.SENSOR_LIST)
        val listOfSensorGroup = QAPreferencesConverter.convertStringToGroupList(json)

        listOfSensorGroup.remove(groupSelected)
        groupSelected.isSelected = isChecked
        listOfSensorGroup.add(groupSelected)
        val string = QAPreferencesConverter.convertGroupListToString(listOfSensorGroup)
        preferencesManager.putString(QAPreferencesKeys.SENSOR_LIST, string)

        //spm?.updateGroup(groupSelected, SharedPreferencesManager.SP_GROUP_ADDRESS_KEY)

        Log.d(TAG, "Device Selected $position is ${groupSelected.name} and is selected: $isChecked")
    }

}