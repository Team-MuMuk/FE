package com.example.mumuk.data.api

import com.example.mumuk.data.model.recipe.ClickLikeRequest
import com.example.mumuk.data.model.recipe.ClickLikeResponse
import com.example.mumuk.data.model.search.UserRecipeDetailResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface UserRecipeApiService {
    @POST("/api/user-recipe/click-like")
    fun clickLike(@Body request: ClickLikeRequest): Call<ClickLikeResponse>

    @GET("/api/user-recipe/{recipeId}")
    suspend fun getUserRecipeDetail(
        @Path("recipeId") recipeId: Long
    ): Response<UserRecipeDetailResponse>
}