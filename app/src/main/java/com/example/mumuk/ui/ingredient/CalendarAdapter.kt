package com.example.mumuk.ui.ingredient

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.mumuk.R
import com.example.mumuk.data.model.DayData

class CalendarAdapter(
    private var days: List<DayData>,
    private val onDayClick: (Int?) -> Unit
) : androidx.recyclerview.widget.RecyclerView.Adapter<CalendarAdapter.DayViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calendar_day, parent, false)
        return DayViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        holder.bind(days[position])
    }

    override fun getItemCount(): Int = days.size

    fun updateDays(newDays: List<DayData>) {
        days = newDays
        notifyDataSetChanged()
    }

    inner class DayViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        fun bind(dayData: DayData) {
            val tvDay = itemView.findViewById<TextView>(R.id.tvDay)
            if (dayData.day == null) {
                tvDay.text = ""
                // 배경 제거
                tvDay.background = null
            } else {
                tvDay.text = dayData.day.toString()
                tvDay.setTextColor(
                    when {
                        dayData.isSelected -> itemView.context.getColor(R.color.blue_default)
                        dayData.isToday -> itemView.context.getColor(R.color.beige_500)
                        else -> itemView.context.getColor(R.color.black)
                    }
                )
                // 선택 시 배경 적용 X, 항상 투명(또는 null)
                tvDay.background = null

                itemView.setOnClickListener { onDayClick(dayData.day) }
            }
        }
    }
}