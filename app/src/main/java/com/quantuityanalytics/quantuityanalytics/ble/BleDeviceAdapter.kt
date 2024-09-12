package com.quantuityanalytics.quantuityanalytics.ble

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.quantuityanalytics.quantuityanalytics.R
import com.quantuityanalytics.quantuityanalytics.adapters.RecycleViewItemInterface

class BleDeviceAdapter(private val dataSet: ArrayList<QABleDevice>,
                       private val recycleViewItemInterface: RecycleViewItemInterface
) :
    RecyclerView.Adapter<BleDeviceAdapter.ViewHolder>()  {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.device_name)
        val descriptionTextView: TextView = view.findViewById(R.id.device_description)
        val detailsTextView: TextView = view.findViewById(R.id.device_details)
        val isSelected: CheckBox = view.findViewById(R.id.device_selected)
        val background: LinearLayout = view.findViewById(R.id.main)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BleDeviceAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_ble_devie, parent, false)
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("MissingPermission", "SetTextI18n")
    override fun onBindViewHolder(holder: BleDeviceAdapter.ViewHolder, position: Int) {
        val currentItem = dataSet[position]

        holder.nameTextView.text = currentItem.deviceName()
        holder.descriptionTextView.text = currentItem.deviceAddress()
        holder.detailsTextView.text = currentItem.deviceAlias() + currentItem.deviceType()
        holder.isSelected.isChecked = currentItem.isSelected


        if (currentItem.isConnected) {
            holder.background.setBackgroundResource(R.color.green)
        } else if (!currentItem.isSelected) {
            holder.background.setBackgroundResource(R.color.white)
        } else {
            holder.background.setBackgroundResource(R.color.primary_light)
        }

        holder.itemView.setOnClickListener {
            recycleViewItemInterface.onDeviceClick(position)
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    fun getDeviceByPosition(position: Int): QABleDevice? {
        if (dataSet.size > position){
            return dataSet[position]
        }
        return null
    }

    fun addDevice(device: QABleDevice) {
        dataSet.add(device)
    }

    fun setDeviceList(listOfDevice: ArrayList<QABleDevice>) {
        dataSet.clear()
        dataSet.addAll(listOfDevice)
    }

    fun selectDevice(position: Int) {
        dataSet[position].isSelected = !dataSet[position].isSelected
        notifyItemChanged(position)
    }

    fun cleanDeviceList() {
        dataSet.clear()
    }

    fun containDevice(device: QABleDevice): Boolean {
        return dataSet.contains(device)
    }
}