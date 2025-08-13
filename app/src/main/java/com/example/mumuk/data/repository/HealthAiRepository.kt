package com.example.mumuk.data.repository

import android.content.Context
import com.example.mumuk.data.api.RetrofitClient
import com.example.mumuk.data.model.Recipe

class HealthAiRecipeRepository(private val context: Context) {
    private val recipeApiService = RetrofitClient.getRecipeApiService(context)

    suspend fun getAiRecipes(): List<Recipe> {
        val response = recipeApiService.getRecommendCombinedRecipes()
        return response.data.map {
            Recipe(
                id = it.recipeId,
                title = it.name,
                recipeImageUrl = it.imageUrl,
                isLiked = it.liked,
                img = null
            )
        }
    }
}