package com.example.mumuk.ui.search

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.mumuk.databinding.ItemSearchAutocompleteBinding

class SearchAutocompleteAdapter(
    private val keywords: List<SearchAutocompleteKeyword>
) : RecyclerView.Adapter<SearchAutocompleteAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemSearchAutocompleteBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSearchAutocompleteBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = keywords[position]
        holder.binding.autocompleteKeywordTv.text = item.text
        holder.binding.autocompleteKeywordTv.setTextColor(
            if (item.isHighlight) Color.BLACK else ContextCompat.getColor(holder.itemView.context, android.R.color.darker_gray)
        )
    }

    override fun getItemCount() = keywords.size
}