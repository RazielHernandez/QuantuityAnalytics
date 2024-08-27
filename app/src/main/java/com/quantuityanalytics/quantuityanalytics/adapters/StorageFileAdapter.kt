package com.quantuityanalytics.quantuityanalytics.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.quantuityanalytics.quantuityanalytics.R
import com.quantuityanalytics.quantuityanalytics.model.StorageFile


class StorageFileAdapter(private val context: Context,
                         private var dataSet: ArrayList<StorageFile>,
                         private val recycleViewItemInterface: RecycleViewItemInterface
) : RecyclerView.Adapter<StorageFileAdapter.ViewHolder>()  {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val fileName: TextView = view.findViewById(R.id.file_name)
        val fileDate: TextView = view.findViewById(R.id.file_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StorageFileAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_file, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = dataSet[position]

        holder.fileName.text = currentItem.name
        holder.fileDate.text = currentItem.date

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

    fun loadFiles(files:ArrayList<StorageFile>) {
        dataSet.clear()
        dataSet = files
    }

    fun getFileName(position: Int): String {
        return dataSet[position].name
    }
}