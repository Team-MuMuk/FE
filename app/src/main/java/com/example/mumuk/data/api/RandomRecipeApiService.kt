package com.example.mumuk.data.api

import com.example.mumuk.data.model.category.RandomRecipeResponse
import retrofit2.Call
import retrofit2.http.GET

interface RandomRecipeApiService {
    @GET("/api/recipe/recommend/random")
    fun getRandomRecipes(): Call<RandomRecipeResponse>
}