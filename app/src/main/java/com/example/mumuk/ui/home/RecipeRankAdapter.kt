package com.example.mumuk.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.example.mumuk.data.model.RecipeRank
import com.example.mumuk.databinding.ItemRankBinding
import com.example.mumuk.R

class RecipeRankAdapter(
    private val onItemClick: (RecipeRank) -> Unit,
    private val onHeartClick: ((RecipeRank, Int) -> Unit)? = null
) : ListAdapter<RecipeRank, RecipeRankAdapter.RecipeRankViewHolder>(DiffCallback) {

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<RecipeRank>() {
            override fun areItemsTheSame(oldItem: RecipeRank, newItem: RecipeRank) =
                oldItem.recipeId == newItem.recipeId
            override fun areContentsTheSame(oldItem: RecipeRank, newItem: RecipeRank) =
                oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeRankViewHolder =
        RecipeRankViewHolder(ItemRankBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: RecipeRankViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    inner class RecipeRankViewHolder(private val binding: ItemRankBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(recipeRank: RecipeRank, position: Int) {
            // Glide로 imageUrl 보여주기, 실패 시 기본 이미지
            val context = binding.img.context
            val defaultImgRes = R.drawable.bg_mosaic

            if (!recipeRank.imageUrl.isNullOrEmpty()) {
                Glide.with(context)
                    .load(recipeRank.imageUrl)
                    .override(240, 240)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .thumbnail(0.1f)
                    .placeholder(defaultImgRes)
                    .error(defaultImgRes)
                    .centerCrop()
                    .into(binding.img)
            } else if (recipeRank.img != null) {
                binding.img.setImageResource(recipeRank.img)
            } else {
                binding.img.setImageResource(defaultImgRes)
            }

            binding.name.text = recipeRank.name
            binding.kcal.text = "${recipeRank.kcal}Kcal"
            binding.rank.text = recipeRank.rank.toString()

            val heartRes = if (recipeRank.isLiked) R.drawable.btn_heart_fill else R.drawable.btn_heart_blank
            binding.bookmarkBtn.setImageResource(heartRes)

            binding.bookmarkBtn.setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    onHeartClick?.invoke(recipeRank, pos)
                }
            }


            binding.root.setOnClickListener { onItemClick(recipeRank) }
        }
    }
}