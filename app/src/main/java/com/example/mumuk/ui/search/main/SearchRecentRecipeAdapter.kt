package com.example.mumuk.ui.search.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mumuk.R
import com.example.mumuk.data.model.search.RecentRecipe
import com.example.mumuk.databinding.ItemRecentRecipeBinding

class SearchRecentRecipeAdapter(
    private val recipes: List<RecentRecipe>,
    private val onItemClick: (RecentRecipe) -> Unit
) : RecyclerView.Adapter<SearchRecentRecipeAdapter.RecipeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val binding = ItemRecentRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecipeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.bind(recipe)
        holder.itemView.setOnClickListener {
            onItemClick(recipe)
        }
    }

    override fun getItemCount(): Int = recipes.size

    class RecipeViewHolder(private val binding: ItemRecentRecipeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(recipe: RecentRecipe) {
            binding.tvRecipeTitle.text = recipe.name

            Glide.with(binding.ivRecipeImage)
                .load(recipe.imageUrl)
                .placeholder(R.drawable.bg_mosaic)
                .into(binding.ivRecipeImage)

            binding.ivRecipeHeart.setImageResource(
                if (recipe.liked) R.drawable.btn_heart_fill else R.drawable.btn_heart_blank
            )
        }
    }
}