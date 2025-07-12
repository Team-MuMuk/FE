package com.example.mumuk.ui.search.result

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mumuk.databinding.ItemCategoryRecipeCardBinding
import com.example.mumuk.ui.category.CategoryRecipeCard

class SearchResultAdapter(
    private val items: List<CategoryRecipeCard>
) : RecyclerView.Adapter<SearchResultAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemCategoryRecipeCardBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCategoryRecipeCardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.recipeTitle.text = item.title
    }

    override fun getItemCount() = items.size
}