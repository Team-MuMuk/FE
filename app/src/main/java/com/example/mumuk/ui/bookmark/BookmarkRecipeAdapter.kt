package com.example.mumuk.ui.bookmark

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mumuk.R
import com.example.mumuk.data.model.Recipe
import com.example.mumuk.databinding.ItemRecipeBinding

class BookmarkRecipeAdapter : ListAdapter<Recipe, BookmarkRecipeAdapter.RecipeViewHolder>(RecipeDiffCallback()) {

    var onItemClick: ((Recipe) -> Unit)? = null

    inner class RecipeViewHolder(private val binding: ItemRecipeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(recipe: Recipe) {
            if (recipe.img != null) {
                binding.recipeImg.setImageResource(recipe.img)
            } else {
                binding.recipeImg.setImageResource(R.drawable.bg_mosaic)
            }
            binding.recipeTitle.text = recipe.title
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val binding =
            ItemRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecipeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val currentRecipe = getItem(position)
        holder.bind(currentRecipe)

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(currentRecipe)
        }
    }

    class RecipeDiffCallback : DiffUtil.ItemCallback<Recipe>() {
        override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
            return oldItem == newItem
        }
    }
}