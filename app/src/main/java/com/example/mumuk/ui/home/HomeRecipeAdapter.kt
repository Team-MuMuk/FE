package com.example.mumuk.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mumuk.R
import com.example.mumuk.data.model.Recipe
import com.example.mumuk.databinding.ItemRecipeBinding

class HomeRecipeAdapter(private val recipes: List<Recipe>, private val onItemClick: (Recipe) -> Unit) :
    RecyclerView.Adapter<HomeRecipeAdapter.RecipeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val binding = ItemRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

    class RecipeViewHolder(private val binding: ItemRecipeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(recipe: Recipe) {
            if (recipe.img != null) {
                binding.recipeImg.setImageResource(recipe.img)
            } else {
                binding.recipeImg.setImageResource(R.drawable.bg_mosaic)
            }
            binding.recipeTitle.text = recipe.title
            if (recipe.isLiked) {
                binding.imageView6.setImageResource(R.drawable.btn_heart_fill)
            } else {
                binding.imageView6.setImageResource(R.drawable.btn_heart_blank)
            }
        }
    }
}