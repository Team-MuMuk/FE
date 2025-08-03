package com.example.mumuk.ui.recipe

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mumuk.R
import com.example.mumuk.databinding.ItemBlogBinding
import com.example.mumuk.data.model.Blog

class BlogAdapter(private var blogs: List<Blog>) :
    RecyclerView.Adapter<BlogAdapter.BlogViewHolder>() {

    inner class BlogViewHolder(private val binding: ItemBlogBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(blog: Blog) {
            binding.title.text = blog.title
            binding.text.text = blog.content
            if (blog.img != null) {
                binding.img.setImageResource(blog.img)
            } else {
                binding.img.setImageResource(R.drawable.bg_mosaic)
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

    fun submitList(newList: List<Blog>) {
        blogs = newList
        notifyDataSetChanged()
    }
}