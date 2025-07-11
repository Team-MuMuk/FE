package com.example.mumuk.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mumuk.databinding.ItemCategoryRecipeCardBinding
import com.example.mumuk.ui.category.CategoryRecipeCard

class SearchRecentRecipeAdapter(
    private val recipes: List<CategoryRecipeCard>
) : RecyclerView.Adapter<SearchRecentRecipeAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemCategoryRecipeCardBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCategoryRecipeCardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount() = recipes.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = recipes[position]
        holder.binding.recipeTitle.text = item.title
    }
}