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
import java.util.Locale


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
        holder.sensor?.text = actualDevice.bleDevice?.address
        holder.result?.text = actualRecord.breakRecord
        //holder.details?.text = actualRecord.value.toString()
        holder.name?.text = actualDevice.deviceName()

        if (actualDevice.status >= QABleDevice.STATUS_CONNECTED) {
            holder.mainSection?.setBackgroundResource(R.color.white)
        } else {
            holder.mainSection?.setBackgroundResource(R.color.light_gray)
        }

        holder.infoSection?.setBackgroundColor(actualRecord.getColorResource(context))
        if (actualRecord.breakRecord.isNotEmpty()){
            holder.guageView?.addNote(actualRecord.createNote(context,holder.guageView!!,actualRecord.getTestResult()), 2000)
            Log.d("TAG", "adding note with result ${actualRecord.breakRecord}")
        }

        if (actualRecord.breakRecord.contains("d1")) {
            holder.guageView?.speedTo(4f)
        } else if (actualRecord.breakRecord.contains("d2")) {
            holder.guageView?.speedTo(3f)
        } else if (actualRecord.breakRecord.contains("d3")) {
            holder.guageView?.speedTo(2f)
        } else if (actualRecord.breakRecord.contains("d4")) {
            holder.guageView?.speedTo(1f)
        } else {
            holder.guageView?.speedTo(0f)
        }

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