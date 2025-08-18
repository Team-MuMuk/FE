package com.example.mumuk.data.api

import com.example.mumuk.data.model.category.CategoryRecipeResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface CategoryRecipeApiService {
    @GET("/api/recipe/recommend/categories/{categories}")
    fun getRecommendedRecipes(
        @Path("categories") categories: String
    ): Call<CategoryRecipeResponse>
}
