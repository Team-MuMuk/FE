package com.example.mumuk.data.api

import com.example.mumuk.data.model.auth.CommonResponse
import com.example.mumuk.data.model.recipe.ClickLikeRequest
import com.example.mumuk.data.model.recipe.ClickLikeResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface UserRecipeApi {
    @POST("/api/user-recipe/click-like")
    fun clickLike(@Body req: ClickLikeRequest): Call<ClickLikeResponse>
}