package com.example.mumuk.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mumuk.data.model.RecipeRank
import com.example.mumuk.databinding.ItemRankBinding
import com.example.mumuk.R

class RecipeRankAdapter(
    private val onItemClick: (RecipeRank) -> Unit,
    private val onHeartClick: ((RecipeRank, Int) -> Unit)? = null
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
        holder.bind(getItem(position), position)
    }

    inner class RecipeRankViewHolder(private val binding: ItemRankBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(recipeRank: RecipeRank, position: Int) {
            recipeRank.img?.let { binding.img.setImageResource(it) }
            binding.name.text = recipeRank.name
            binding.kcal.text = "${recipeRank.kcal}Kcal"
            binding.rank.text = recipeRank.rank.toString()

            val heartRes = if (recipeRank.isLiked) R.drawable.btn_heart_fill else R.drawable.btn_heart_blank
            binding.bookmarkBtn.setImageResource(heartRes)

            binding.bookmarkBtn.setOnClickListener {
                recipeRank.isLiked = !recipeRank.isLiked
                notifyItemChanged(position)
                onHeartClick?.invoke(recipeRank, position)
            }

            binding.root.setOnClickListener { onItemClick(recipeRank) }
        }
    }
}