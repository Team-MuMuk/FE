package com.example.mumuk.ui.recipe

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mumuk.data.model.NutritionInfo
import com.example.mumuk.databinding.ItemInfoBinding

class NutritionInfoAdapter : ListAdapter<NutritionInfo, NutritionInfoAdapter.InfoViewHolder>(InfoDiffCallback()) {

    inner class InfoViewHolder(private val binding: ItemInfoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(info: NutritionInfo) {
            binding.infoType.text = info.type
            binding.infoAmount.text = info.amount
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfoViewHolder {
        val binding = ItemInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return InfoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InfoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class InfoDiffCallback : DiffUtil.ItemCallback<NutritionInfo>() {
        override fun areItemsTheSame(oldItem: NutritionInfo, newItem: NutritionInfo): Boolean {
            return oldItem.type == newItem.type
        }

        override fun areContentsTheSame(oldItem: NutritionInfo, newItem: NutritionInfo): Boolean {
            return oldItem == newItem
        }
    }
}