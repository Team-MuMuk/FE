package com.example.mumuk.data.model.search

data class RecipeSearchItem(
    val recipeId: Long,
    val name: String,
    val imgResId: Int?,
    val liked: Boolean
)