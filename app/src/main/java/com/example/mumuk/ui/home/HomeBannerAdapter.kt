package com.example.mumuk.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mumuk.R
import com.example.mumuk.data.model.Banner

class HomeBannerAdapter(
    private val items: List<Banner>,
    private val onBannerClick: ((position: Int) -> Unit)? = null
) : RecyclerView.Adapter<HomeBannerAdapter.BannerViewHolder>() {

    val loopItems: List<Banner> = if (items.size > 1)
        listOf(items.last()) + items + listOf(items.first())
    else
        items

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_home_banner, parent, false)
        return BannerViewHolder(view)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        val item = loopItems[position]
        holder.image.setImageResource(item.imageResId)
        holder.text.text = item.text

        holder.itemView.setOnClickListener {
            onBannerClick?.invoke(position)
        }
    }

    override fun getItemCount() = loopItems.size

    class BannerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.bannerImage)
        val text: TextView = view.findViewById(R.id.bannerText)
    }
}