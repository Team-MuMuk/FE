package com.example.mumuk.ui.alarm

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mumuk.R
import com.example.mumuk.data.model.alarm.AlarmItem
import java.text.SimpleDateFormat
import java.util.Locale

class AlarmAdapter(private var items: List<AlarmItem>) :
    RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder>() {

    inner class AlarmViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val alarmContent: TextView = itemView.findViewById(R.id.text_alarm_content)
        val expiryLabel: TextView = itemView.findViewById(R.id.text_expiry_label)
        val expiryDate: TextView = itemView.findViewById(R.id.text_expiry_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_alarm_chip, parent, false)
        return AlarmViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val item = items[position]
        val message = item.body
            .replace("의 유통기한", "의\n유통기한")
            .replace("'", "")
        holder.alarmContent.text = message
        holder.expiryLabel.text = "유통기한"
        holder.expiryDate.text = formatDate(item.createdAt)
    }

    override fun getItemCount(): Int = items.size

    fun updateList(newItems: List<AlarmItem>) {
        val filteredItems = newItems.distinctBy { it.body }
        items = filteredItems
        notifyDataSetChanged()
    }

    private fun formatDate(dateString: String): String {
        return try {
            val srcFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val destFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
            val date = srcFormat.parse(dateString)
            if (date != null) destFormat.format(date) else dateString.substring(0, 10).replace("-", ".")
        } catch (e: Exception) {
            dateString.substring(0, 10).replace("-", ".")
        }
    }
}