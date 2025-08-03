package com.example.mumuk.data.model.search

data class UserRecipeDetailResponse(
    val status: String,
    val code: String,
    val message: String,
    val data: UserRecipeDetailData
)