package com.example.mumuk.ui.alarm

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mumuk.R
import com.example.mumuk.data.model.alarm.AlarmItem

class AlarmAdapter(private var items: List<AlarmItem>) :
    RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder>() {

    inner class AlarmViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.text_alarm_content)
        val status = itemView.findViewById<TextView>(R.id.text_expiry_label)
        val createdAt = itemView.findViewById<TextView>(R.id.text_expiry_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_alarm_chip, parent, false)
        return AlarmViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val item = items[position]
        holder.title.text = item.title
        holder.status.text = item.status
        holder.createdAt.text = item.createdAt.substring(0, 10)
    }

    override fun getItemCount(): Int = items.size

    fun updateList(newItems: List<AlarmItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}