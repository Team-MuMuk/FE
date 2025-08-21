package com.example.mumuk.ui.recommend

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.example.mumuk.data.model.Recipe
import com.example.mumuk.databinding.ItemRecipeBinding
import com.example.mumuk.data.api.RetrofitClient
import com.example.mumuk.data.model.recipe.ClickLikeRequest
import com.example.mumuk.data.model.recipe.ClickLikeResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.bumptech.glide.load.DataSource

class HealthAiAdapter(
    private var items: MutableList<Recipe>,
    private val onItemClick: (Recipe) -> Unit,
    private val onImageLoaded: () -> Unit
) : RecyclerView.Adapter<HealthAiAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemRecipeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(recipe: Recipe, position: Int) {
            binding.recipeTitle.text = recipe.title

            recipe.recipeImageUrl?.let {
                Glide.with(binding.recipeImg.context)
                    .load(it)
                    .placeholder(com.example.mumuk.R.drawable.bg_mosaic)
                    .error(com.example.mumuk.R.drawable.bg_mosaic)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: com.bumptech.glide.request.target.Target<Drawable?>,
                            isFirstResource: Boolean
                        ): Boolean {
                            onImageLoaded()
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable,
                            model: Any,
                            target: com.bumptech.glide.request.target.Target<Drawable?>?,
                            dataSource: DataSource,
                            isFirstResource: Boolean
                        ): Boolean {
                            onImageLoaded()
                            return false
                        }
                    })
                    .into(binding.recipeImg)
            }

            binding.imageView6.setImageResource(
                if (recipe.isLiked) com.example.mumuk.R.drawable.btn_heart_fill
                else com.example.mumuk.R.drawable.btn_heart_blank
            )
            binding.imageView6.setOnClickListener {
                val context = binding.root.context
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
            binding.root.setOnClickListener {
                onItemClick(recipe)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], position)
    }

    override fun getItemCount(): Int = items.size

    fun updateList(newItems: List<Recipe>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}