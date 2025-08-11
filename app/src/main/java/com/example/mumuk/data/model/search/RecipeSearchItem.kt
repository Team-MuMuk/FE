package com.example.mumuk.data.model.search

data class RecipeSearchItem(
    val recipeId: Long,
    val name: String,
    val imageUrl: String?,
    val liked: Boolean
)