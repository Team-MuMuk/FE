package com.example.mumuk.data.api

import com.example.mumuk.data.model.ingredient.IngredientDdaySettingRequest
import com.example.mumuk.data.model.ingredient.IngredientDdaySettingResponse
import com.example.mumuk.data.model.ingredient.IngredientDeleteResponse
import com.example.mumuk.data.model.ingredient.IngredientExpireDateUpdateRequest
import com.example.mumuk.data.model.ingredient.IngredientQuantityUpdateRequest
import com.example.mumuk.data.model.ingredient.IngredientRegisterRequest
import com.example.mumuk.data.model.ingredient.IngredientRegisterResponse
import com.example.mumuk.data.model.ingredient.IngredientResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface IngredientApiService {
    @GET("/api/ingredient/retrieve")
    suspend fun getIngredients(): Response<IngredientResponse>

    @POST("/api/ingredient/register")
    suspend fun registerIngredient(
        @Body request: IngredientRegisterRequest
    ): Response<IngredientRegisterResponse>

    @DELETE("/api/ingredient/{ingredientId}/delete")
    suspend fun deleteIngredient(
        @Path("ingredientId") ingredientId: Int
    ): Response<IngredientDeleteResponse>

    @PUT("/api/ingredient/{ingredientId}/quantity")
    suspend fun updateIngredientQuantity(
        @Path("ingredientId") ingredientId: Int,
        @Body request: IngredientQuantityUpdateRequest
    ): Response<Unit>

    @PUT("/api/ingredient/{ingredientId}/expiredate")
    suspend fun updateIngredientExpireDate(
        @Path("ingredientId") ingredientId: Int,
        @Body request: IngredientExpireDateUpdateRequest
    ): Response<IngredientDeleteResponse>

    @PUT("/api/ingredient/{ingredientId}/dday-setting")
    suspend fun updateIngredientDdaySetting(
        @Path("ingredientId") ingredientId: Int,
        @Body request: IngredientDdaySettingRequest
    ): Response<IngredientDdaySettingResponse>
}