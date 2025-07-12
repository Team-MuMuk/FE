package com.example.mumuk.ui.search.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mumuk.databinding.ItemSearchPopularKeywordBinding

class SearchPopularAdapter(
    private val keywords: List<String>
) : RecyclerView.Adapter<SearchPopularAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemSearchPopularKeywordBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSearchPopularKeywordBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount() = keywords.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.searchPopularRankTv.text = (position + 1).toString()
        holder.binding.searchPopularKeywordTv.text = keywords[position]
    }
}