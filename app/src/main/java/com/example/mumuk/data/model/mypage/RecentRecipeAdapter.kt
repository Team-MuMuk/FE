package com.example.mumuk.data.model.mypage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mumuk.R
import com.example.mumuk.databinding.ItemRecipeBinding
import com.example.mumuk.ui.mypage.RecentRecipe

class RecentRecipeAdapter(
    private val items: MutableList<RecentRecipe>,
    private val onItemClick: (RecentRecipe) -> Unit,
    private val onHeartClick: (RecentRecipe, Int) -> Unit
) : RecyclerView.Adapter<RecentRecipeAdapter.VH>() {

    inner class VH(val binding: ItemRecipeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RecentRecipe, pos: Int) = with(binding) {
            // 홈/검색과 동일한 바인딩 id 사용
            recipeTitle.text = item.name
            Glide.with(recipeImg.context)
                .load(item.image)
                .placeholder(R.drawable.bg_mosaic)
                .error(R.drawable.bg_mosaic)
                .into(recipeImg)

            imageView6.setImageResource(
                if (item.liked) R.drawable.btn_heart_fill else R.drawable.btn_heart_blank
            )

            root.setOnClickListener { onItemClick(item) }
            imageView6.setOnClickListener { onHeartClick(item, pos) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position], position)
    }

    override fun getItemCount(): Int = items.size

    fun submitList(newItems: List<RecentRecipe>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun updateLikeAt(position: Int, liked: Boolean) {
        if (position in items.indices) {
            val old = items[position]
            items[position] = old.copy(liked = liked)
            notifyItemChanged(position)
        }
    }
}
