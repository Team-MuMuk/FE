package com.example.mumuk.data.repository

import android.content.Context
import com.example.mumuk.data.api.RetrofitClient
import com.example.mumuk.data.model.Recipe

class IngredientAiRecipeRepository(private val context: Context) {
    private val recipeApiService = RetrofitClient.getRecipeApiService(context)

    suspend fun getAiRecipes(): List<Recipe> {
        val response = recipeApiService.getRecommendIngredientRecipes()
        // API 응답 데이터를 앱에서 사용하는 Recipe 모델로 변환
        return response.data.map {
            Recipe(
                id = it.recipeId,
                title = it.name,
                recipeImageUrl = it.imageUrl,
                isLiked = it.liked,
                img = null // API에서 이미지 URL을 사용하므로 기존 drawable id는 null로 설정
            )
        }
    }
}