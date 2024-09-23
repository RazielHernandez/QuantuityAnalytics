package com.quantuityanalytics.quantuityanalytics.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.quantuityanalytics.quantuityanalytics.R
import com.quantuityanalytics.quantuityanalytics.ble.QABleDevice
import com.quantuityanalytics.quantuityanalytics.model.SensorMacAddress

class AddressAdapter(
    private val context: Context,
    private var dataSet: ArrayList<SensorMacAddress>,
    private val recycleViewItemInterface: RecycleViewItemInterface)
        : RecyclerView.Adapter<AddressAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_address, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = dataSet[position]

        holder.address.text = currentItem.address

        holder.deleteButton.setOnClickListener {
            recycleViewItemInterface.onDeviceClick(position)
        }
    }

    fun addItem(address: SensorMacAddress): ArrayList<SensorMacAddress> {
        dataSet.add(address)
        notifyItemInserted(dataSet.indexOf(address))
        return dataSet
    }

    fun deleteItemAt(position: Int): ArrayList<SensorMacAddress> {
        dataSet.removeAt(position)
        notifyDataSetChanged()
        return dataSet
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val address: TextView = view.findViewById(R.id.macAddress)
        val deleteButton: ImageButton = view.findViewById(R.id.deleteButton)
    }


}