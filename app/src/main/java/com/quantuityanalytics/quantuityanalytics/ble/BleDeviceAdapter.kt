package com.quantuityanalytics.quantuityanalytics.ble

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.quantuityanalytics.quantuityanalytics.R

class BleDeviceAdapter(context: Context, data: ArrayList<BleDevice>) :
    RecyclerView.Adapter<BleDeviceAdapter.ViewHolder>()  {
    private val dataSet: ArrayList<BleDevice> = data
    var mContext: Context = context

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Define click listener for the ViewHolder's View
        val nameTextView: TextView = view.findViewById(R.id.device_name)
        val descriptionTextView: TextView = view.findViewById(R.id.device_description)
        val detailsTextView: TextView = view.findViewById(R.id.device_details)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BleDeviceAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.ble_devie_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: BleDeviceAdapter.ViewHolder, position: Int) {
        val currentItem = dataSet[position]

        holder.nameTextView.text = currentItem.deviceName
        holder.descriptionTextView.text = currentItem.deviceDescription
        holder.detailsTextView.text = currentItem.deviceDetails
    }


    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    fun addDevice(device: BleDevice) {
        dataSet.add(device)
    }

    fun cleanDeviceList() {
        dataSet.clear()
    }

    fun containDevice(device: BleDevice): Boolean {
        return dataSet.contains(device)
    }
}