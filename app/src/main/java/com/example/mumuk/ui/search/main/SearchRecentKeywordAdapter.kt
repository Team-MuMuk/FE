package com.example.mumuk.ui.search.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mumuk.data.model.search.RecentSearch
import com.example.mumuk.databinding.ItemSearchRecentKeywordChipBinding

class SearchRecentKeywordAdapter(
    private val keywords: MutableList<RecentSearch>,
    private val onKeywordClick: (String) -> Unit,
    private val onDeleteClick: (RecentSearch, Int) -> Unit
) : RecyclerView.Adapter<SearchRecentKeywordAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemSearchRecentKeywordChipBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RecentSearch) {
            binding.searchRecentKeywordTv.text = item.title
            binding.searchRecentKeywordTv.setOnClickListener {
                onKeywordClick(item.title ?: "")
            }
            binding.searchRecentKeywordDeleteBtn.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onDeleteClick(item, position)
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

    fun removeAt(position: Int) {
        keywords.removeAt(position)
        notifyItemRemoved(position)
    }
}