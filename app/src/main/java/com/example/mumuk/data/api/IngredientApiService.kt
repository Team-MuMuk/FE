package com.example.mumuk.data.api

import com.example.mumuk.data.model.ingredient.IngredientResponse
import retrofit2.Response
import retrofit2.http.GET

interface IngredientApiService {
    @GET("/api/ingredient/retrieve")
    suspend fun getIngredients(): Response<IngredientResponse>
}