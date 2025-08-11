package com.example.mumuk.data.api

import com.example.mumuk.data.model.search.RecipeSearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RecipeSearchApiService {
    @GET("/api/search/search")
    fun searchRecipes(@Query("keyword") keyword: String): Call<RecipeSearchResponse>
}
