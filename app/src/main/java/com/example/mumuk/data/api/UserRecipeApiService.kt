package com.example.mumuk.data.api

import com.example.mumuk.data.model.bookmark.LikedRecipeResponse
import com.example.mumuk.data.model.recipe.BlogResponse
import com.example.mumuk.data.model.recipe.ClickLikeRequest
import com.example.mumuk.data.model.recipe.ClickLikeResponse
import com.example.mumuk.data.model.search.UserRecipeDetailResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface UserRecipeApiService {
    @POST("/api/user-recipe/click-like")
    fun clickLike(@Body request: ClickLikeRequest): Call<ClickLikeResponse>

    @GET("/api/user-recipe/{recipeId}")
    suspend fun getUserRecipeDetail(
        @Path("recipeId") recipeId: Long
    ): Response<UserRecipeDetailResponse>

    @GET("/api/recipe/search-blog")
    suspend fun searchBlogs(
        @Query("keyword") keyword: String
    ): Response<BlogResponse>

    @GET("/api/user-recipe/liked-recipe")
    fun getLikedRecipes(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<LikedRecipeResponse>

}