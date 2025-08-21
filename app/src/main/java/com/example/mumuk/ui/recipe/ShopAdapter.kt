package com.example.mumuk.ui.recipe

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mumuk.databinding.ItemShopBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.mumuk.data.model.recipe.NaverShoppingItem

class ShopAdapter : ListAdapter<NaverShoppingItem, ShopAdapter.ShopViewHolder>(ShopDiffCallback()) {

    inner class ShopViewHolder(private val binding: ItemShopBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: NaverShoppingItem) {
            Glide.with(binding.shopImg.context)
                .load(item.imageUrl)
                .override(400, 400)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .thumbnail(0.1f)
                .into(binding.shopImg)
            binding.shopTitle.text = item.title
            binding.textView8.text = "가격: ${item.price}원"
            binding.root.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.link))
                binding.root.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopViewHolder {
        val binding =
            ItemShopBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShopViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShopViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ShopDiffCallback : DiffUtil.ItemCallback<NaverShoppingItem>() {
        override fun areItemsTheSame(oldItem: NaverShoppingItem, newItem: NaverShoppingItem): Boolean {
            return oldItem.link == newItem.link // 링크가 고유하다고 가정
        }

        override fun areContentsTheSame(oldItem: NaverShoppingItem, newItem: NaverShoppingItem): Boolean {
            return oldItem == newItem
        }
    }
}