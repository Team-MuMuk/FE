package com.example.mumuk.data.model.mypage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mumuk.R
import com.example.mumuk.databinding.ItemRecipeBinding

class RecentRecipeAdapter(
    private val items: MutableList<RecentRecipe>,
    private val onItemClick: (RecentRecipe) -> Unit,
    private val onHeartClick: (RecentRecipe, Int) -> Unit
) : RecyclerView.Adapter<RecentRecipeAdapter.VH>() {

    companion object { private const val PAYLOAD_LIKE = "payload_like" }

    inner class VH(val binding: ItemRecipeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RecentRecipe) = with(binding) {
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

            imageView6.setOnClickListener {
                val p = bindingAdapterPosition
                if (p != RecyclerView.NO_POSITION) {
                    onHeartClick(items[p], p)
                }
            }
        }

        fun bindLikeOnly(isLiked: Boolean) {
            binding.imageView6.setImageResource(
                if (isLiked) R.drawable.btn_heart_fill else R.drawable.btn_heart_blank
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(ItemRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position])
    }

    override fun onBindViewHolder(holder: VH, position: Int, payloads: MutableList<Any>) {
        if (payloads.contains(PAYLOAD_LIKE)) {
            holder.bindLikeOnly(items[position].liked)
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    override fun getItemCount(): Int = items.size

    fun submitList(newItems: List<RecentRecipe>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun updateLikeAt(position: Int, liked: Boolean) {
        if (position !in items.indices) return
        val old = items[position]
        items[position] = old.copy(liked = liked)
        notifyItemChanged(position, PAYLOAD_LIKE)
    }
}
