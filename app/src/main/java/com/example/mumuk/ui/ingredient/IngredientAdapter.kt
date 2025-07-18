package com.example.mumuk.ui.ingredient

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mumuk.data.model.Ingredient
import com.example.mumuk.databinding.ItemIngredientBinding

class IngredientAdapter(private val items: List<Ingredient>) :
    RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder>() {

    inner class IngredientViewHolder(val binding: ItemIngredientBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val binding = ItemIngredientBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return IngredientViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        val item = items[position]
        holder.binding.textView31.text = item.name
        holder.binding.textView32.text = "유통기한: ${item.expiryDate}"
    }

    override fun getItemCount(): Int = items.size
}