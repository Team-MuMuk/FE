package com.example.mumuk.data.api

import com.example.mumuk.data.model.recipe.ClickLikeRequest
import com.example.mumuk.data.model.recipe.ClickLikeResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface UserRecipeApiService {
    @POST("/api/user-recipe/click-like")
    fun clickLike(@Body request: ClickLikeRequest): Call<ClickLikeResponse>
}