package com.example.mumuk.ui.recommend

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mumuk.data.model.Recipe
import com.example.mumuk.databinding.ItemRecipeBinding

class AiRecipeAdapter(
    private var items: List<Recipe>
) : RecyclerView.Adapter<AiRecipeAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemRecipeBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recipe = items[position]
        holder.binding.recipeTitle.text = recipe.title
        recipe.img?.let {
            holder.binding.recipeImg.setImageResource(it)
        }
        holder.binding.imageView6.setImageResource(
            if (recipe.isLiked) com.example.mumuk.R.drawable.btn_heart_fill
            else com.example.mumuk.R.drawable.btn_heart_blank
        )
    }

    override fun getItemCount(): Int = items.size

    fun updateList(newItems: List<Recipe>) {
        items = newItems
        notifyDataSetChanged()
    }
}