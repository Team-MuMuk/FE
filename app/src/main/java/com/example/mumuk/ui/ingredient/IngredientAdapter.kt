package com.example.mumuk.ui.ingredient

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mumuk.data.model.Ingredient
import com.example.mumuk.databinding.ItemIngredientBinding

class IngredientAdapter(
    private var items: List<Ingredient>,
    private val onItemClick: (Ingredient) -> Unit,
    private val onDeleteClick: (Ingredient) -> Unit
) : RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder>() {

    inner class IngredientViewHolder(val binding: ItemIngredientBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Ingredient) {
            binding.name.text = item.name
            binding.date.text = "유통기한: ${item.expiryDate}"
            binding.root.setOnClickListener {
                onItemClick(item)
            }
            binding.delBtn.setOnClickListener {
                onDeleteClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val binding = ItemIngredientBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return IngredientViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun submitList(newItems: List<Ingredient>) {
        items = newItems
        notifyDataSetChanged()
    }
}