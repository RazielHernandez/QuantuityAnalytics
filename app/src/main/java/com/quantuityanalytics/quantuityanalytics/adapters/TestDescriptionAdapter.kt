package com.quantuityanalytics.quantuityanalytics.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.quantuityanalytics.quantuityanalytics.R
import com.quantuityanalytics.quantuityanalytics.model.TestDescriptionItem

class TestDescriptionAdapter(private val data: List<TestDescriptionItem>):
    RecyclerView.Adapter<TestDescriptionAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.description_image)
        val textView: TextView = itemView.findViewById(R.id.description_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.test_description, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val actualItem = data[position]
        holder.textView.text = actualItem.description
        holder.imageView.setImageResource(actualItem.image)
    }
}