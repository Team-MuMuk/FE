package com.example.mumuk.ui.home

import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
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
    private val onItemClick: (Recipe) -> Unit,
    private val onHeartClick: (Recipe, Int) -> Unit
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
            val pos = holder.bindingAdapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                onHeartClick(recipe, pos)
            }
        }
    }

    fun updateLikeAt(pos: Int, liked: Boolean) {
        if (pos !in recipes.indices) return
        recipes[pos] = recipes[pos].copy(isLiked = liked)
        notifyItemChanged(pos)
    }

    fun submitAll(newList: List<Recipe>) {
        recipes.clear()
        recipes.addAll(newList)
        notifyDataSetChanged()
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
                .override(450, 450)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .thumbnail(0.1f)
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