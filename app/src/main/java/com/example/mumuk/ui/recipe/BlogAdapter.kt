package com.example.mumuk.ui.recipe

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.mumuk.R
import com.example.mumuk.databinding.ItemBlogBinding
import com.example.mumuk.data.model.recipe.SearchedBlog

class BlogAdapter(private var blogs: List<SearchedBlog>) :
    RecyclerView.Adapter<BlogAdapter.BlogViewHolder>() {

    inner class BlogViewHolder(private val binding: ItemBlogBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(blog: SearchedBlog) {
            binding.title.text = Html.fromHtml(blog.title, Html.FROM_HTML_MODE_LEGACY).toString()
            val descriptionText = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(blog.description, Html.FROM_HTML_MODE_LEGACY)
            } else {
                @Suppress("DEPRECATION")
                Html.fromHtml(blog.description)
            }
            binding.text.text = descriptionText

            Glide.with(binding.img.context)
                .load(blog.ogImageUrl)
                .override(300, 300)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .thumbnail(0.1f)
                .error(R.drawable.bg_mosaic)
                .into(binding.img)

            binding.root.setOnClickListener {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(blog.link))
                    binding.root.context.startActivity(intent)
                } catch (e: Exception) {
                    // 잘못된 URL 등 예외 처리
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogViewHolder {
        val binding = ItemBlogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BlogViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BlogViewHolder, position: Int) {
        holder.bind(blogs[position])
    }

    override fun getItemCount(): Int = blogs.size

    fun submitList(newList: List<SearchedBlog>) {
        blogs = newList
        notifyDataSetChanged()
    }
}