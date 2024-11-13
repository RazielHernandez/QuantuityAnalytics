package com.quantuityanalytics.quantuityanalytics.ble

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.quantuityanalytics.quantuityanalytics.R
import com.quantuityanalytics.quantuityanalytics.adapters.RecycleViewItemInterface

class BleDeviceAdapter(private val model: String,
                       private val context: Context,
                       private val dataSet: ArrayList<QABleDevice>,
                       private val recycleViewItemInterface: RecycleViewItemInterface
) :
    RecyclerView.Adapter<BleDeviceAdapter.ViewHolder>()  {

        var isEnable: Boolean = true

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.device_name)
        val descriptionTextView: TextView = view.findViewById(R.id.device_description)
        val detailsTextView: TextView = view.findViewById(R.id.device_details)
        val isSelected: CheckBox = view.findViewById(R.id.device_selected)
        val background: LinearLayout = view.findViewById(R.id.main)
        val resultImage: ImageView = view.findViewById(R.id.device_result)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BleDeviceAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_ble_device, parent, false)
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("MissingPermission", "SetTextI18n")
    override fun onBindViewHolder(holder: BleDeviceAdapter.ViewHolder, position: Int) {
        val currentItem = dataSet[position]

        holder.nameTextView.text = currentItem.deviceName()
        holder.descriptionTextView.text = currentItem.deviceAddress()
        holder.detailsTextView.text = currentItem.deviceAlias() + currentItem.deviceType()
        holder.isSelected.isChecked = currentItem.status >= QABleDevice.STATUS_CONNECTED

        when (currentItem.status) {
            QABleDevice.STATUS_ERROR -> {
                holder.background.setBackgroundResource(R.color.red)
            }
            QABleDevice.STATUS_UNREACHABLE -> {
                holder.background.setBackgroundResource(R.color.light_gray)
            }
            QABleDevice.STATUS_DISCOVERED -> {
                holder.background.setBackgroundResource(R.color.white)
            }
            QABleDevice.STATUS_CONNECTED -> {
                holder.background.setBackgroundResource(R.color.primary_light)
            }
            QABleDevice.STATUS_READING -> {
                holder.background.setBackgroundResource(R.color.green)
            }
        }

        if (currentItem.hasRecords()) {
            holder.resultImage.visibility = View.VISIBLE
            val actualRecord = currentItem.getLastRecord()

            holder.resultImage.setImageDrawable(actualRecord.getColoredIcon(model, context))
        } else {
            holder.resultImage.visibility = View.INVISIBLE
        }

        holder.itemView.setOnClickListener {
            if (isEnable){
                recycleViewItemInterface.onDeviceClick(position)
            }
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
        //dataSet[position].isSelected = !dataSet[position].isSelected
        //notifyItemChanged(position)
    }

    fun cleanDeviceList() {
        dataSet.clear()
    }

    fun containDevice(device: QABleDevice): Boolean {
        return dataSet.contains(device)
    }
}