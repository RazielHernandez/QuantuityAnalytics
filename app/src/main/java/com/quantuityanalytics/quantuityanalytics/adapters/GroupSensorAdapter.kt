package com.quantuityanalytics.quantuityanalytics.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.quantuityanalytics.quantuityanalytics.R
import com.quantuityanalytics.quantuityanalytics.model.SensorGroup

class GroupSensorAdapter(
    private val context: Context,
    private var dataSet: ArrayList<SensorGroup>,
    private val recycleViewItemInterface: RecycleViewItemInterface)
    : RecyclerView.Adapter<GroupSensorAdapter.ViewHolder>()  {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupSensorAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_sensor_group, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = dataSet[position]

        holder.name.text = currentItem.name
        holder.checkBox.isChecked = currentItem.isSelected


        holder.layout.setBackgroundResource(if (currentItem.isSelected) R.color.primary_light else R.color.white )

        holder.layout.setOnClickListener {
            recycleViewItemInterface.onDeviceClick(position)
        }

        holder.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->

            holder.layout.setBackgroundResource(if (isChecked) R.color.primary_light else R.color.white )
            recycleViewItemInterface.onDeviceSelected(position, isChecked)
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    fun addItem(group: SensorGroup): ArrayList<SensorGroup> {
        dataSet.add(group)
        notifyItemInserted(dataSet.indexOf(group))
        return dataSet
    }

    fun setDataSet(groups: ArrayList<SensorGroup>) {
        dataSet = groups
        notifyDataSetChanged()
    }

    fun deleteItemAt(position: Int): ArrayList<SensorGroup> {
        dataSet.removeAt(position)
        notifyDataSetChanged()
        return dataSet
    }

    fun getItemAt(position: Int): SensorGroup {
        return dataSet[position]
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.group_name)
        val layout: LinearLayout = view.findViewById(R.id.main)
        val checkBox: CheckBox = view.findViewById(R.id.group_selected)
    }
}