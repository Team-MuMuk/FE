package com.example.mumuk.data.api

import com.example.mumuk.data.model.recommend.BaseResponse
import com.example.mumuk.data.model.recommend.RecommendRecipeResponse
import retrofit2.http.GET

interface RecipeApiService {
    @GET("/api/v1/recipe/recommend/ingredient")
    suspend fun getRecommendIngredientRecipes(): BaseResponse<List<RecommendRecipeResponse>>
}