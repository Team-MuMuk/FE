package com.example.mumuk.ui.bookmark

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mumuk.R
import com.example.mumuk.data.model.Recipe
import com.example.mumuk.databinding.ItemRecipeBinding

class BookmarkRecipeAdapter
    : ListAdapter<Recipe, BookmarkRecipeAdapter.RecipeViewHolder>(RecipeDiffCallback()) {

    var onItemClick: ((Recipe) -> Unit)? = null
    var onHeartClick: ((Recipe) -> Unit)? = null

    init { setHasStableIds(true) }
    override fun getItemId(position: Int) = getItem(position).id


    inner class RecipeViewHolder(val binding: ItemRecipeBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(recipe: Recipe) {
            val url = recipe.recipeImageUrl

            when {
                !url.isNullOrEmpty() -> {
                    Glide.with(binding.recipeImg.context)
                        .load(url)
                        .placeholder(R.drawable.bg_mosaic)
                        .error(R.drawable.bg_mosaic)
                        .into(binding.recipeImg)
                }
                recipe.img != null -> {
                    Glide.with(binding.recipeImg.context).clear(binding.recipeImg)
                    binding.recipeImg.setImageResource(recipe.img)
                }
                else -> {
                    Glide.with(binding.recipeImg.context).clear(binding.recipeImg)
                    binding.recipeImg.setImageResource(R.drawable.bg_mosaic)
                }
            }

            binding.recipeTitle.text = recipe.title
            binding.imageView6.setImageResource(
                if (recipe.isLiked) R.drawable.btn_heart_fill else R.drawable.btn_heart_blank
            )

            binding.imageView6.setImageResource(
                if (recipe.isLiked) R.drawable.btn_heart_fill else R.drawable.btn_heart_blank
            )

            binding.imageView6.setOnClickListener { onHeartClick?.invoke(recipe) }
            binding.root.setOnClickListener { onItemClick?.invoke(recipe) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val binding = ItemRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecipeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class RecipeDiffCallback : DiffUtil.ItemCallback<Recipe>() {
        override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe) = oldItem == newItem
    }
}
