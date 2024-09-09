package com.quantuityanalytics.quantuityanalytics.adapters


import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.quantuityanalytics.quantuityanalytics.R
import com.quantuityanalytics.quantuityanalytics.model.BreakRecord


class GaugeViewAdapter(private val context: Context, private var dataset: ArrayList<BreakRecord>) : BaseAdapter() {

    fun addItem(breakRecord: BreakRecord) {
        dataset.add(breakRecord)
        notifyDataSetChanged()
    }

    fun addItems(records: ArrayList<BreakRecord>) {
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
            Log.d("TAG", "Building view for position $position")
            view = LayoutInflater.from(context).inflate(R.layout.view_gauge, parent, false)
            holder = ViewHolder()
            holder.imageView = view.findViewById(R.id.image)
            holder.sensor = view.findViewById(R.id.sensor)
            holder.result = view.findViewById(R.id.result)
            holder.details = view.findViewById(R.id.details)
            view.tag = holder
        } else {
            Log.d("TAG", "Null view for position $position")
            holder = view.tag as ViewHolder
        }

        // Set the image and text for the current grid item
        val actualItem = dataset[position]
        holder.sensor?.text = actualItem.sensorId
        holder.result?.text = actualItem.breakRecord
        holder.details?.text = actualItem.value.toString()

        return view!!
    }

    private class ViewHolder {
        var imageView: ImageView? = null
        var sensor: TextView? = null
        var result: TextView? = null
        var details: TextView? = null
    }


}