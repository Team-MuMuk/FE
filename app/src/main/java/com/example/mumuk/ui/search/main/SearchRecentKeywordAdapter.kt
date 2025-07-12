package com.example.mumuk.ui.search.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mumuk.databinding.ItemSearchRecentKeywordChipBinding

class SearchRecentKeywordAdapter(
    private val keywords: MutableList<String>,
    private val onKeywordClick: (String) -> Unit
) : RecyclerView.Adapter<SearchRecentKeywordAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemSearchRecentKeywordChipBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(keyword: String) {
            binding.searchRecentKeywordTv.text = keyword
            binding.searchRecentKeywordTv.setOnClickListener {
                onKeywordClick(keyword)
            }
            binding.searchRecentKeywordDeleteBtn.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    keywords.removeAt(position)
                    notifyItemRemoved(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSearchRecentKeywordChipBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount() = keywords.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(keywords[position])
    }
}