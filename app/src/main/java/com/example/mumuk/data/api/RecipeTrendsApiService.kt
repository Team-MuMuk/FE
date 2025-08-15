package com.example.mumuk.data.api

import com.example.mumuk.data.model.recipe.RecipeTrendResponse
import retrofit2.Response
import retrofit2.http.GET

interface RecipeTrendsApiService {
    @GET("/api/search/trends/recipe-detail")
    suspend fun getRecipeTrends(): Response<RecipeTrendResponse>
}