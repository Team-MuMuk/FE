package com.example.mumuk.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mumuk.data.model.RecipeRank
import com.example.mumuk.databinding.ItemRankBinding

class RecipeRankAdapter(
    private val onItemClick: (RecipeRank) -> Unit
) : ListAdapter<RecipeRank, RecipeRankAdapter.RecipeRankViewHolder>(DiffCallback) {

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<RecipeRank>() {
            override fun areItemsTheSame(oldItem: RecipeRank, newItem: RecipeRank) = oldItem.rank == newItem.rank
            override fun areContentsTheSame(oldItem: RecipeRank, newItem: RecipeRank) = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeRankViewHolder =
        RecipeRankViewHolder(ItemRankBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: RecipeRankViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class RecipeRankViewHolder(private val binding: ItemRankBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(recipeRank: RecipeRank) {
            recipeRank.img?.let { binding.img.setImageResource(it) }
            binding.name.text = recipeRank.name
            binding.kcal.text = "${recipeRank.kcal}Kcal"
            binding.rank.text = recipeRank.rank.toString()
            binding.root.setOnClickListener { onItemClick(recipeRank) }
        }
    }
}