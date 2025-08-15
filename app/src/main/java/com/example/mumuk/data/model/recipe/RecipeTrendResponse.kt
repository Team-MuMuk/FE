package com.example.mumuk.data.model.recipe

data class RecipeTrendResponse(
    val status: String?,
    val code: String?,
    val message: String?,
    val data: List<RecipeTrendItem>
)

data class RecipeTrendItem(
    val recipeId: Int,
    val title: String,
    val imageUrl: String,
    val calories: Int,
    val liked: Boolean
)