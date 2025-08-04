package com.example.mumuk.data.model.category

data class CategoryRecipeResponse(
    val status: String,
    val code: String,
    val message: String,
    val data: List<CategoryRecipe>
)