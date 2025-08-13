package com.example.mumuk.ui.recommend

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mumuk.data.model.Recipe
import com.example.mumuk.databinding.ItemRecipeBinding

class HealthAiAdapter(
    private var items: MutableList<Recipe>,
    private val onItemClick: (Recipe) -> Unit,
    private val onHeartClick: ((Recipe, Int) -> Unit)? = null // 하트 클릭 리스너 추가
) : RecyclerView.Adapter<HealthAiAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemRecipeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(recipe: Recipe, position: Int) {
            binding.recipeTitle.text = recipe.title

            // Glide를 사용하여 이미지 URL로부터 이미지를 로드합니다.
            recipe.recipeImageUrl?.let {
                Glide.with(binding.recipeImg.context)
                    .load(it)
                    .placeholder(com.example.mumuk.R.drawable.bg_mosaic) // 로딩 중 표시할 이미지
                    .error(com.example.mumuk.R.drawable.bg_mosaic) // 에러 시 표시할 이미지
                    .into(binding.recipeImg)
            }

            binding.imageView6.setImageResource(
                if (recipe.isLiked) com.example.mumuk.R.drawable.btn_heart_fill
                else com.example.mumuk.R.drawable.btn_heart_blank
            )
            binding.imageView6.setOnClickListener {
                recipe.isLiked = !recipe.isLiked
                notifyItemChanged(position)
                onHeartClick?.invoke(recipe, position)
            }
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
        holder.bind(items[position], position)
    }

    override fun getItemCount(): Int = items.size

    fun updateList(newItems: List<Recipe>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}