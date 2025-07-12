package com.example.mumuk.ui.category

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mumuk.databinding.ItemCategoryRecipeCardBinding

class CategoryRecipeCardAdapter(
    private val items: List<CategoryRecipeCard>,
    private val onItemClick: ((CategoryRecipeCard) -> Unit)? = null
) : RecyclerView.Adapter<CategoryRecipeCardAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemCategoryRecipeCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CategoryRecipeCard) {
            binding.recipeTitle.text = item.title

            binding.root.setOnClickListener {
                onItemClick?.invoke(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCategoryRecipeCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}