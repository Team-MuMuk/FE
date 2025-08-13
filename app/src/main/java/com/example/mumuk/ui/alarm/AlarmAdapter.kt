package com.example.mumuk.ui.alarm

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mumuk.R

class AlarmAdapter(private val items: List<AlarmItem>) :
    RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder>() {

    inner class AlarmViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val content = itemView.findViewById<TextView>(R.id.text_alarm_content)
        val expiryLabel = itemView.findViewById<TextView>(R.id.text_expiry_label)
        val expiryDate = itemView.findViewById<TextView>(R.id.text_expiry_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_alarm_chip, parent, false)
        return AlarmViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val item = items[position]
        holder.content.text = item.content
        holder.expiryLabel.text = item.expiryLabel
        holder.expiryDate.text = item.expiryDate
    }

    override fun getItemCount(): Int = items.size
}