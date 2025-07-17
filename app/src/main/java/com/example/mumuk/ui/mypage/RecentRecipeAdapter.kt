package com.example.mumuk.ui.mypage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mumuk.R
import com.example.mumuk.databinding.ItemRecentRecipeBinding

class RecentRecipeAdapter(
    private val recipes: List<RecentRecipe>,
    private val onItemClick: (RecentRecipe) -> Unit
) : RecyclerView.Adapter<RecentRecipeAdapter.RecipeViewHolder>() {

    inner class RecipeViewHolder(val binding: ItemRecentRecipeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(recipes[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val binding = ItemRecentRecipeBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return RecipeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[position]
        with(holder.binding) {
            tvRecipeTitle.text = recipe.title
            ivRecipeImage.setImageResource(recipe.imageResId)
            ivRecipeHeart.setImageResource(
                if (recipe.liked) R.drawable.ic_heart_full else R.drawable.ic_heart_empty
            )
        }
    }

    override fun getItemCount(): Int = recipes.size
}
