package com.example.mumuk.data.model.category

data class RandomRecipeResponse(
    val status: String,
    val code: String,
    val message: String,
    val data: List<RandomRecipe>
)