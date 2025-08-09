package com.example.mumuk.ui.search.autocomplete

import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mumuk.databinding.ItemSearchAutocompleteBinding

class SearchAutocompleteAdapter(
    private val keywords: List<SearchAutocompleteKeyword>,
    private val query: String,
    private val onItemClick: (String) -> Unit
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
        holder.binding.autocompleteKeywordTv.text = getSpannable(item.text, query)
        holder.itemView.setOnClickListener {
            onItemClick(item.text)
        }
    }

    override fun getItemCount() = keywords.size

    private fun getSpannable(fullText: String, query: String): SpannableString {
        if (query.isBlank()) return SpannableString(fullText)
        val startIdx = fullText.indexOf(query, ignoreCase = true)
        if (startIdx < 0) return SpannableString(fullText)
        val endIdx = startIdx + query.length
        val spannable = SpannableString(fullText)
        spannable.setSpan(
            StyleSpan(Typeface.BOLD),
            startIdx, endIdx,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return spannable
    }
}