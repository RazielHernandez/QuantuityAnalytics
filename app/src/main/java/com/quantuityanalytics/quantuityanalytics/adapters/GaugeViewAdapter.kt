package com.quantuityanalytics.quantuityanalytics.adapters


import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.github.anastr.speedviewlib.SpeedView
import com.github.anastr.speedviewlib.components.note.Note
import com.github.anastr.speedviewlib.components.note.TextNote
import com.quantuityanalytics.quantuityanalytics.R
import com.quantuityanalytics.quantuityanalytics.ble.QABleDevice
import com.quantuityanalytics.quantuityanalytics.ble.QABleRecord
import java.util.Locale


class GaugeViewAdapter(
    private val model: String,
    private val context: Context,
    private var dataset: ArrayList<QABleDevice>) : BaseAdapter() {

    companion object {
        const val TAG = "QuantuityAnalytics.GaugeViewAdapter"
    }

    fun addItem(breakRecord: QABleDevice) {
        dataset.add(breakRecord)
        notifyDataSetChanged()
    }

    fun addItems(devices: ArrayList<QABleDevice>) {
        dataset.addAll(devices)
        notifyDataSetChanged()
    }

    fun updateItem(device: QABleDevice) {
        val position = dataset.indexOf(device)
        if (position >= 0){
            Log.d(TAG, "Updating device ${device.deviceAddress()}")
            dataset[position] = device
            notifyDataSetChanged()
        }
    }

    fun getItemPosition(device: QABleDevice): Int {
        return dataset.indexOf(device)
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
            view = LayoutInflater.from(context).inflate(R.layout.view_gauge, parent, false)
            holder = ViewHolder()

            holder.sensor = view.findViewById(R.id.sensor)
            holder.result = view.findViewById(R.id.result)
            holder.infoSection = view.findViewById(R.id.info_section)
            holder.mainSection = view.findViewById(R.id.main)
            holder.name = view.findViewById(R.id.name)
            holder.guageView = view.findViewById(R.id.gaugeView)
            view.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }


        val actualDevice = dataset[position]
        val actualRecord = actualDevice.getLastRecord()
        holder.sensor?.text = actualDevice.bleDevice.address
        holder.result?.text = actualRecord.breakRecord
        holder.name?.text = actualDevice.deviceName()
        holder.guageView?.setMinMaxSpeed(0F, QABleRecord.MAX_SPEED)

        if (actualDevice.status >= QABleDevice.STATUS_CONNECTED) {
            holder.mainSection?.setBackgroundResource(R.color.white)
        } else {
            holder.mainSection?.setBackgroundResource(R.color.light_gray)
        }

        holder.infoSection?.setBackgroundColor(actualRecord.getColorResource(model, context))
        if (actualRecord.breakRecord.isNotEmpty()){
            holder.guageView?.addNote(actualRecord.createNote(model, context,holder.guageView!!,actualRecord.getTestResult(model)), 2000)
            Log.d(TAG, "adding note with result ${actualRecord.breakRecord}")
        }

        holder.guageView?.speedTo(actualRecord.getSpeed(model))

        return view!!
    }

    private class ViewHolder {
        //var imageView: ImageView? = null
        var guageView: SpeedView? = null
        var name: TextView? = null
        var sensor: TextView? = null
        var result: TextView? = null
        //var details: TextView? = null
        var infoSection: LinearLayout? = null
        var mainSection: LinearLayout? = null
    }


}