package com.example.mumuk.data.model.category

data class RandomRecipe(
    val id: Long,
    val title: String,
    val recipeImage: String,
    val liked: Boolean = false
)