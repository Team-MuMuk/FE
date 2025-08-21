package com.example.mumuk.ui.home

import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.load.engine.GlideException
import com.example.mumuk.R
import com.example.mumuk.data.model.Recipe
import com.example.mumuk.databinding.ItemRecipeBinding
import com.example.mumuk.data.api.RetrofitClient
import com.example.mumuk.data.model.recipe.ClickLikeRequest
import com.example.mumuk.data.model.recipe.ClickLikeResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeRecipeAdapter(
    private val recipes: MutableList<Recipe>,
    private val onItemClick: (Recipe) -> Unit
) : RecyclerView.Adapter<HomeRecipeAdapter.RecipeViewHolder>() {

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

        // Glide retry count
        private val MAX_RETRY = 3

        fun bind(recipe: Recipe) {
            when {
                !recipe.recipeImageUrl.isNullOrEmpty() -> {
                    loadImageWithRetry(recipe.recipeImageUrl, 0)
                }
                recipe.img != null -> {
                    binding.recipeImg.setImageResource(recipe.img)
                }
                else -> {
                    binding.recipeImg.setImageResource(R.drawable.bg_mosaic)
                }
            }
            binding.recipeTitle.text = recipe.title
            binding.imageView6.setImageResource(
                if (recipe.isLiked) R.drawable.btn_heart_fill else R.drawable.btn_heart_blank
            )
        }

        private fun loadImageWithRetry(url: String, retryCount: Int) {
            Glide.with(binding.recipeImg.context)
                .load(url)
                .placeholder(R.drawable.bg_mosaic)
                .error(R.drawable.bg_mosaic)
                .listener(object : RequestListener<android.graphics.drawable.Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable?>,
                        isFirstResource: Boolean
                    ): Boolean {
                        if (retryCount < MAX_RETRY) {
                            Handler(Looper.getMainLooper()).postDelayed({
                                loadImageWithRetry(url, retryCount + 1)
                            }, 500)
                        }
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable?>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }
                })
                .into(binding.recipeImg)
        }
    }
}