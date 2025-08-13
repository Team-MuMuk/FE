package com.example.mumuk.data.model.recommend

import com.google.gson.annotations.SerializedName

data class RecommendRecipeResponse(
    @SerializedName("recipeId") val recipeId: Long,
    @SerializedName("name") val name: String,
    @SerializedName("imageUrl") val imageUrl: String,
    @SerializedName("liked") val liked: Boolean
)

data class BaseResponse<T>(
    val status: String,
    val code: String,
    val message: String,
    val data: T
)