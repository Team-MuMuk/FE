package com.example.mumuk.ui.search.result

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mumuk.data.model.Recipe
import com.example.mumuk.data.api.RetrofitClient
import com.example.mumuk.data.model.recipe.ClickLikeRequest
import com.example.mumuk.data.model.recipe.ClickLikeResponse
import com.example.mumuk.databinding.ItemRecipeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.bumptech.glide.Glide

class SearchResultAdapter(
    private val items: MutableList<Recipe>,
    private val onItemClick: (Recipe) -> Unit
) : RecyclerView.Adapter<SearchResultAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemRecipeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Recipe) {
            binding.recipeTitle.text = item.title

            item.img?.let {
                binding.recipeImg.setImageResource(it)
            } ?: item.recipeImageUrl?.let { url ->
                Glide.with(binding.recipeImg.context)
                    .load(url)
                    .into(binding.recipeImg)
            } ?: binding.recipeImg.setImageDrawable(null)

            binding.imageView6.setImageResource(
                if (item.isLiked) com.example.mumuk.R.drawable.btn_heart_fill
                else com.example.mumuk.R.drawable.btn_heart_blank
            )

            binding.root.setOnClickListener {
                onItemClick(item)
            }

            binding.imageView6.setOnClickListener {
                val context = binding.root.context
                item.isLiked = !item.isLiked
                notifyItemChanged(adapterPosition)

                val api = RetrofitClient.getUserRecipeApi(context)
                val request = ClickLikeRequest(recipeId = item.id)
                api.clickLike(request).enqueue(object : Callback<ClickLikeResponse> {
                    override fun onResponse(
                        call: Call<ClickLikeResponse>,
                        response: Response<ClickLikeResponse>
                    ) {
                    }

                    override fun onFailure(
                        call: Call<ClickLikeResponse>,
                        t: Throwable
                    ) {
                    }
                })
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRecipeBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    fun updateList(newItems: List<Recipe>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}