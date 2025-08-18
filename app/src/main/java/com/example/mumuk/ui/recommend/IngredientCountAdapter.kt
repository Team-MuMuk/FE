package com.example.mumuk.ui.recommend

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mumuk.data.model.Ingredient
import com.example.mumuk.databinding.ItemIngredientCountBinding

class IngredientCountAdapter(
    private val items: List<Ingredient>,
    private val onQuantityChanged: (ingredientId: Int, newCount: Int) -> Unit
) : RecyclerView.Adapter<IngredientCountAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemIngredientCountBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemIngredientCountBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ingredient = items[position]
        with(holder.binding) {
            name.text = ingredient.name
            date.text = "유통기한: ${ingredient.expiryDate}"
            count.text = ingredient.count.toString()

            btnMinus.setOnClickListener {
                if (ingredient.count > 1) {
                    ingredient.count--
                    count.text = ingredient.count.toString()
                    onQuantityChanged(ingredient.id, ingredient.count)
                }
            }
            btnPlus.setOnClickListener {
                ingredient.count++
                count.text = ingredient.count.toString()
                onQuantityChanged(ingredient.id, ingredient.count)
            }
        }
    }

    override fun getItemCount(): Int = items.size
}