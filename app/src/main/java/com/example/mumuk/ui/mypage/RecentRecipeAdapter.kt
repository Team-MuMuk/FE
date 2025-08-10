package com.example.mumuk.ui.mypage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
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
                val pos = bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) onItemClick(recipes[pos])
            }
        }

        fun bind(recipe: RecentRecipe, position: Int) = with(binding) {
            tvRecipeTitle.text = recipe.name

            Glide.with(root)
                .load(recipe.image)
                .placeholder(R.drawable.bg_mosaic)
                .error(R.drawable.bg_mosaic)
                .centerCrop()
                .into(ivRecipeImage)

            ivRecipeHeart.setImageResource(
                if (recipe.liked) R.drawable.btn_heart_fill else R.drawable.btn_heart_blank
            )

            ivRecipeHeart.setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos == RecyclerView.NO_POSITION) return@setOnClickListener
                recipes[pos] = recipes[pos].copy(liked = !recipes[pos].liked)
                notifyItemChanged(pos)
                onHeartClick?.invoke(recipes[pos], pos)
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

    fun submitList(newItems: List<RecentRecipe>) {
        recipes.clear()
        recipes.addAll(newItems)
        notifyDataSetChanged()
    }
}
