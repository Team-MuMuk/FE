package com.example.mumuk.data.api

import com.example.mumuk.data.model.ingredient.IngredientRegisterRequest
import com.example.mumuk.data.model.ingredient.IngredientRegisterResponse
import com.example.mumuk.data.model.ingredient.IngredientResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface IngredientApiService {
    @GET("/api/ingredient/retrieve")
    suspend fun getIngredients(): Response<IngredientResponse>

    @POST("/api/ingredient/register")
    suspend fun registerIngredient(
        @Body request: IngredientRegisterRequest
    ): Response<IngredientRegisterResponse>
}