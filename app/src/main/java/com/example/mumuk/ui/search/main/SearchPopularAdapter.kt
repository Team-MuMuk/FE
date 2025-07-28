package com.example.mumuk.ui.search.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mumuk.databinding.ItemSearchPopularKeywordBinding

class SearchPopularAdapter(
    private val keywords: List<String>,
    private val onKeywordClick: ((String) -> Unit)? = null
) : RecyclerView.Adapter<SearchPopularAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemSearchPopularKeywordBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(keyword: String, position: Int) {
            binding.searchPopularRankTv.text = (position + 1).toString()
            binding.searchPopularKeywordTv.text = keyword
            binding.root.setOnClickListener {
                onKeywordClick?.invoke(keyword)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSearchPopularKeywordBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount() = keywords.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(keywords[position], position)
    }
}