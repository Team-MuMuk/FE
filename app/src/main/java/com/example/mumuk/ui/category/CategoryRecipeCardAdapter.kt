package com.example.mumuk.ui.category

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mumuk.R
import com.example.mumuk.data.model.Recipe
import com.example.mumuk.databinding.ItemRecipeBinding
import com.example.mumuk.data.api.RetrofitClient
import com.example.mumuk.data.model.recipe.ClickLikeRequest
import com.example.mumuk.data.model.recipe.ClickLikeResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategoryRecipeCardAdapter(
    private val recipes: MutableList<Recipe>,
    private val onItemClick: (Recipe) -> Unit
) : RecyclerView.Adapter<CategoryRecipeCardAdapter.RecipeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val binding = ItemRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecipeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.bind(recipe)
        holder.itemView.setOnClickListener {
            onItemClick(recipe)
        }
        holder.binding.imageView6.setOnClickListener {
            val context = holder.binding.root.context
            recipe.isLiked = !recipe.isLiked
            notifyItemChanged(position)

            val api = RetrofitClient.getUserRecipeApi(context)
            val request = ClickLikeRequest(recipeId = recipe.id)
            api.clickLike(request).enqueue(object : Callback<ClickLikeResponse> {
                override fun onResponse(
                    call: Call<ClickLikeResponse>,
                    response: Response<ClickLikeResponse>
                ) {}
                override fun onFailure(call: Call<ClickLikeResponse>, t: Throwable) {}
            })
        }
    }

    override fun getItemCount(): Int = recipes.size

    class RecipeViewHolder(val binding: ItemRecipeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(recipe: Recipe) {
            if (!recipe.recipeImageUrl.isNullOrEmpty()) {
                Glide.with(binding.recipeImg.context)
                    .load(recipe.recipeImageUrl)
                    .placeholder(R.drawable.bg_mosaic)
                    .error(R.drawable.bg_mosaic)
                    .into(binding.recipeImg)
            } else if (recipe.img != null) {
                binding.recipeImg.setImageResource(recipe.img)
            } else {
                binding.recipeImg.setImageResource(R.drawable.bg_mosaic)
            }
            binding.recipeTitle.text = recipe.title ?: ""
            binding.imageView6.setImageResource(
                if (recipe.isLiked) R.drawable.btn_heart_fill else R.drawable.btn_heart_blank
            )
        }
    }
}