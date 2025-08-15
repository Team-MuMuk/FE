package com.example.mumuk.data.api

import com.example.mumuk.data.model.recipe.NaverShoppingItem
import retrofit2.http.GET
import retrofit2.http.Path

data class NaverShoppingResponse(
    val naverShoppings: List<NaverShoppingItem>
)

interface NaverShoppingApiService {
    @GET("/api/recipe/search-naver-shopping/{recipeId}")
    suspend fun getNaverShoppings(@Path("recipeId") recipeId: Long): NaverShoppingResponse
}