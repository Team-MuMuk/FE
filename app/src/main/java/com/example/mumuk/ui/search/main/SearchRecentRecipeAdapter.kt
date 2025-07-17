package com.example.mumuk.ui.search.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mumuk.data.model.Recipe
import com.example.mumuk.databinding.ItemRecipeBinding

class SearchRecentRecipeAdapter(
    private val recipes: List<Recipe>
) : RecyclerView.Adapter<SearchRecentRecipeAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemRecipeBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRecipeBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount() = recipes.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = recipes[position]
        holder.binding.recipeTitle.text = item.title
        holder.binding.recipeImg.setImageResource(item.imageResId)
        holder.binding.imageView6.setImageResource(
            if (item.isLiked) com.example.mumuk.R.drawable.btn_heart_blank
            else com.example.mumuk.R.drawable.btn_heart_blank
        )
    }
}