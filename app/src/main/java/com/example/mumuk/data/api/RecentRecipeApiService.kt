package com.example.mumuk.data.api

import com.example.mumuk.data.model.search.RecentRecipeResponse
import retrofit2.Call
import retrofit2.http.GET

interface RecentRecipeApiService {
    @GET("/api/user-recipe/recent")
    fun getRecentRecipes(): Call<RecentRecipeResponse>
}