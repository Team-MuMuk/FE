// RecentRecipeAdapter.kt
package com.example.mumuk.data.model.mypage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mumuk.R
import com.example.mumuk.databinding.ItemRecentRecipeBinding
import com.example.mumuk.ui.mypage.RecentRecipe

class RecentRecipeAdapter(
    private val items: MutableList<RecentRecipe>,
    private val onItemClick: (RecentRecipe) -> Unit,
    private val onHeartClick: (RecentRecipe, Int) -> Unit
) : RecyclerView.Adapter<RecentRecipeAdapter.VH>() {

    inner class VH(val binding: ItemRecentRecipeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RecentRecipe) = with(binding) {
            tvRecipeTitle.text = item.name

            ivRecipeHeart.isSelected = item.liked
            ivRecipeHeart.setImageResource(
                if (item.liked) R.drawable.btn_heart_fill else R.drawable.btn_heart_blank
            )

            Glide.with(root)
                .load(item.image)
                .placeholder(R.drawable.bg_mosaic)
                .error(R.drawable.bg_mosaic)
                .centerCrop()
                .into(ivRecipeImage)

            root.setOnClickListener { onItemClick(item) }
            ivRecipeHeart.setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) onHeartClick(item, pos)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val inf = LayoutInflater.from(parent.context)
        val binding = ItemRecentRecipeBinding.inflate(inf, parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(items[position])
    override fun getItemCount(): Int = items.size

    fun submitList(newItems: List<RecentRecipe>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}
