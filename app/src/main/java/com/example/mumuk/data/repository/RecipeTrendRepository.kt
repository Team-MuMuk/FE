package com.example.mumuk.data.repository

import android.content.Context
import com.example.mumuk.data.api.RetrofitClient
import com.example.mumuk.data.model.RecipeRank

class RecipeTrendRepository {
    suspend fun getRecipeTrendRank(context: Context): List<RecipeRank> {
        val api = RetrofitClient.getRecipeTrendsApi(context)
        val response = api.getRecipeTrends()
        return if (response.isSuccessful && response.body()?.data != null) {
            response.body()!!.data.mapIndexed { idx, item ->
                RecipeRank(
                    recipeId = item.recipeId,
                    img = null,
                    imageUrl = item.imageUrl, // 서버에서 받은 url 전달
                    name = item.title,
                    kcal = item.calories,
                    rank = idx + 1,
                    isLiked = item.liked
                )
            }
        } else {
            emptyList()
        }
    }
}