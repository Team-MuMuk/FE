package com.example.mumuk.data.model.search

data class RecentRecipeResponse(
    val status: String,
    val code: String,
    val message: String,
    val data: RecentRecipeData
)