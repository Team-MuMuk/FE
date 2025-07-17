package com.example.mumuk.ui.search.result

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mumuk.data.model.Recipe
import com.example.mumuk.databinding.ItemRecipeBinding

class SearchResultAdapter(
    private val items: List<Recipe>
) : RecyclerView.Adapter<SearchResultAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemRecipeBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRecipeBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.recipeTitle.text = item.title
        holder.binding.recipeImg.setImageResource(item.imageResId)
        holder.binding.imageView6.setImageResource(
            if (item.isLiked) com.example.mumuk.R.drawable.btn_heart_blank
            else com.example.mumuk.R.drawable.btn_heart_blank
        )
    }

    override fun getItemCount() = items.size
}