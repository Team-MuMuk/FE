package com.example.mumuk.data.api

import com.example.mumuk.data.model.bookmark.LikedRecipeResponse
import com.example.mumuk.data.model.recipe.ClickLikeRequest
import com.example.mumuk.data.model.recipe.ClickLikeResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface UserRecipeApi {
    @POST("/api/user-recipe/click-like")
    fun clickLike(@Body req: ClickLikeRequest): Call<ClickLikeResponse>

    @GET("/api/user-recipe/liked-recipe")
    suspend fun getLikedRecipes(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<LikedRecipeResponse>
}