package com.example.mumuk.ui.ingredient

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mumuk.data.model.Ingredient
import com.example.mumuk.databinding.ItemIngredientExpiringBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class ExpiringIngredientAdapter(
    private val items: List<Ingredient>,
    private val onItemClick: (Ingredient) -> Unit
) : RecyclerView.Adapter<ExpiringIngredientAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemIngredientExpiringBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Ingredient) {
            binding.name.text = item.name
            binding.date.text = "유통기한: ${item.expiryDate}"

            // D-Day 계산
            val today = LocalDate.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val expiry = LocalDate.parse(item.expiryDate, formatter)
            val dDayNum = ChronoUnit.DAYS.between(today, expiry)
            binding.dDay.text = "D - $dDayNum"

            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemIngredientExpiringBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}