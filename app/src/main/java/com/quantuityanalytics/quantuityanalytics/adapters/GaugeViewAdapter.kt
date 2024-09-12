package com.quantuityanalytics.quantuityanalytics.adapters


import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.quantuityanalytics.quantuityanalytics.R
import com.quantuityanalytics.quantuityanalytics.ble.QABleDevice
import com.quantuityanalytics.quantuityanalytics.ble.QABleRecord
import com.quantuityanalytics.quantuityanalytics.model.BreakRecord


class GaugeViewAdapter(private val context: Context, private var dataset: ArrayList<QABleDevice>) : BaseAdapter() {

    fun addItem(breakRecord: QABleDevice) {
        dataset.add(breakRecord)
        notifyDataSetChanged()
    }

    fun addItems(records: ArrayList<QABleDevice>) {
        dataset.addAll(records)
        notifyDataSetChanged()
    }

    fun clearItems() {
        dataset.clear()
    }

    override fun getCount(): Int {
        return dataset.size
    }

    override fun getItem(position: Int): Any {
        return dataset[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        val holder: ViewHolder

        if (view == null) {
            //Log.d("TAG", "Building view for position $position")
            view = LayoutInflater.from(context).inflate(R.layout.view_gauge, parent, false)
            holder = ViewHolder()
            holder.imageView = view.findViewById(R.id.image)
            holder.sensor = view.findViewById(R.id.sensor)
            holder.result = view.findViewById(R.id.result)
            holder.details = view.findViewById(R.id.details)
            holder.infoSection = view.findViewById(R.id.info_section)
            holder.mainSection = view.findViewById(R.id.main)
            view.tag = holder
        } else {
            //Log.d("TAG", "Null view for position $position")
            holder = view.tag as ViewHolder
        }


        val actualDevice = dataset[position]
        val actualRecord = actualDevice.getLastRecord()
        holder.sensor?.text = actualDevice.bleDevice?.address
        holder.result?.text = actualRecord.breakRecord
        holder.details?.text = actualRecord.value.toString()

        if (actualDevice.isConnected) {
            holder.mainSection?.setBackgroundResource(R.color.primary_light)
        } else {
            holder.mainSection?.setBackgroundResource(R.color.light_gray)
        }

        if (actualRecord.breakRecord.contains("d1")) {
            holder.infoSection?.setBackgroundResource(R.color.red)
            holder.imageView?.setImageResource(R.drawable.break_red)
        } else if (actualRecord.breakRecord.contains("d2")) {
            holder.infoSection?.setBackgroundResource(R.color.orange)
            holder.imageView?.setImageResource(R.drawable.break_orange)
        } else if (actualRecord.breakRecord.contains("d3")) {
            holder.infoSection?.setBackgroundResource(R.color.yellow)
            holder.imageView?.setImageResource(R.drawable.break_yellow)
        } else if (actualRecord.breakRecord.contains("d4")) {
            holder.infoSection?.setBackgroundResource(R.color.green)
            holder.imageView?.setImageResource(R.drawable.break_green)
        } else {
            holder.infoSection?.setBackgroundResource(R.color.primary_light)
            holder.imageView?.setImageResource(R.drawable.warningsignal)
        }



        // Set the image and text for the current grid item
//        val actualItem = dataset[position]
//        holder.sensor?.text = actualItem.sensorId
//        holder.result?.text = actualItem.breakRecord
//        holder.details?.text = actualItem.value.toString()
//
//        if (actualItem.status == BreakRecord.STATUS_NOT_FOUND) {
//            holder.mainSection?.setBackgroundResource(R.color.light_gray)
//        } else if (actualItem.status == BreakRecord.STATUS_DISCONNECTED) {
//            holder.mainSection?.setBackgroundResource(R.color.primary_light)
//        } else if (actualItem.breakRecord.contains("d1")) {
//            holder.infoSection?.setBackgroundResource(R.color.red)
//            holder.imageView?.setImageResource(R.drawable.break_red)
//        } else if (actualItem.breakRecord.contains("d2")) {
//            holder.infoSection?.setBackgroundResource(R.color.orange)
//            holder.imageView?.setImageResource(R.drawable.break_orange)
//        } else if (actualItem.breakRecord.contains("d3")) {
//            holder.infoSection?.setBackgroundResource(R.color.yellow)
//            holder.imageView?.setImageResource(R.drawable.break_yellow)
//        } else if (actualItem.breakRecord.contains("d4")) {
//            holder.infoSection?.setBackgroundResource(R.color.green)
//            holder.imageView?.setImageResource(R.drawable.break_green)
//        } else {
//            holder.infoSection?.setBackgroundResource(R.color.primary_light)
//            holder.imageView?.setImageResource(R.drawable.warningsignal)
//        }

        return view!!
    }

    private class ViewHolder {
        var imageView: ImageView? = null
        var sensor: TextView? = null
        var result: TextView? = null
        var details: TextView? = null
        var infoSection: LinearLayout? = null
        var mainSection: LinearLayout? = null
    }


}