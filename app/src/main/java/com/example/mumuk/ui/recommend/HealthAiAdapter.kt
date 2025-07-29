package com.example.mumuk.ui.recommend

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mumuk.data.model.Recipe
import com.example.mumuk.databinding.ItemRecipeBinding

class HealthAiAdapter(
    private var items: List<Recipe>,
    private val onItemClick: (Recipe) -> Unit // 클릭 리스너 추가
) : RecyclerView.Adapter<HealthAiAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemRecipeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(recipe: Recipe) {
            binding.recipeTitle.text = recipe.title
            recipe.img?.let {
                binding.recipeImg.setImageResource(it)
            }
            binding.imageView6.setImageResource(
                if (recipe.isLiked) com.example.mumuk.R.drawable.btn_heart_fill
                else com.example.mumuk.R.drawable.btn_heart_blank
            )
            binding.root.setOnClickListener {
                onItemClick(recipe)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun updateList(newItems: List<Recipe>) {
        items = newItems
        notifyDataSetChanged()
    }
}