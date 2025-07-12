package com.example.mumuk.ui.category

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mumuk.R

class CategoryRecipeCardAdapter(
    private val items: List<CategoryRecipeCard>,
    private val onItemClick: ((CategoryRecipeCard) -> Unit)? = null
) : RecyclerView.Adapter<CategoryRecipeCardAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.recipe_title)

        fun bind(item: CategoryRecipeCard) {
            title.text = item.title

            itemView.setOnClickListener {
                onItemClick?.invoke(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category_recipe_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
