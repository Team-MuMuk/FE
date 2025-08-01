package com.example.mumuk.ui.mypage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mumuk.R
import com.example.mumuk.databinding.ItemRecentRecipeBinding

class RecentRecipeAdapter(
    private val recipes: MutableList<RecentRecipe>,
    private val onItemClick: (RecentRecipe) -> Unit,
    private val onHeartClick: ((RecentRecipe, Int) -> Unit)? = null
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
        fun bind(recipe: RecentRecipe, position: Int) {
            binding.tvRecipeTitle.text = recipe.title
            binding.ivRecipeImage.setImageResource(recipe.imageResId)
            binding.ivRecipeHeart.setImageResource(
                if (recipe.liked) R.drawable.ic_heart_full else R.drawable.ic_heart_empty
            )

            binding.ivRecipeHeart.setOnClickListener {
                recipes[position].liked = !recipes[position].liked
                notifyItemChanged(position)
                onHeartClick?.invoke(recipes[position], position)
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
        holder.bind(recipes[position], position)
    }

    override fun getItemCount(): Int = recipes.size
}