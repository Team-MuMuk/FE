package com.example.mumuk.ui.recipe

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mumuk.data.model.RecipeIngredient
import com.example.mumuk.databinding.ItemIngredientAvailableBinding
import com.example.mumuk.databinding.ItemIngredientMissingBinding

class IngredientAdapter(
    private var ingredients: List<RecipeIngredient>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_AVAILABLE = 0
        private const val TYPE_MISSING = 1
    }

    inner class AvailableViewHolder(val binding: ItemIngredientAvailableBinding)
        : RecyclerView.ViewHolder(binding.root)

    inner class MissingViewHolder(val binding: ItemIngredientMissingBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun getItemViewType(position: Int): Int {
        // 'inFridge' 대신 코틀린 속성 이름인 'isAvailable'을 사용합니다.
        return if (ingredients[position].isAvailable) TYPE_AVAILABLE else TYPE_MISSING
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_AVAILABLE) {
            val binding = ItemIngredientAvailableBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            AvailableViewHolder(binding)
        } else {
            val binding = ItemIngredientMissingBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            MissingViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val ingredient = ingredients[position]
        if (holder is AvailableViewHolder) {
            // ItemIngredientAvailableBinding의 TextView ID가 ingredientText라고 가정합니다.
            holder.binding.ingredientText.text = ingredient.name
        } else if (holder is MissingViewHolder) {
            // ItemIngredientMissingBinding의 TextView ID가 ingredientText라고 가정합니다.
            holder.binding.ingredientText.text = ingredient.name
        }
    }

    override fun getItemCount(): Int = ingredients.size

    // RecipeFragment에서 호출할 데이터 업데이트 함수
    fun updateData(newIngredients: List<RecipeIngredient>) {
        this.ingredients = newIngredients
        notifyDataSetChanged()
    }
}