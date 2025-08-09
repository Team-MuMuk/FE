package com.example.mumuk.ui.search.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mumuk.R
import com.example.mumuk.data.model.search.RecentRecipe
import com.example.mumuk.databinding.ItemRecipeBinding
import com.example.mumuk.data.api.RetrofitClient
import com.example.mumuk.data.model.recipe.ClickLikeRequest
import com.example.mumuk.data.model.recipe.ClickLikeResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchRecentRecipeAdapter(
    private val recipes: MutableList<RecentRecipe>,
    private val onItemClick: (RecentRecipe) -> Unit
) : RecyclerView.Adapter<SearchRecentRecipeAdapter.RecipeViewHolder>() {

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
            val newLiked = !recipe.liked
            recipe.liked = newLiked
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
        fun bind(recipe: RecentRecipe) {
            binding.recipeTitle.text = recipe.title
            Glide.with(binding.recipeImg)
                .load(recipe.imageUrl)
                .placeholder(R.drawable.bg_mosaic)
                .into(binding.recipeImg)
            binding.imageView6.setImageResource(
                if (recipe.liked) R.drawable.btn_heart_fill else R.drawable.btn_heart_blank
            )
        }
    }
}