package com.example.mumuk.data.model.recipe

data class RecipeIngredientMatchResponse(
    val status: String,
    val code: String,
    val message: String,
    val data: IngredientMatchData
)

data class IngredientMatchData(
    val recipeId: Long,
    val recipeTitle: String,
    val match: List<String>,
    val mismatch: List<String>,
    val replaceable: List<ReplaceableIngredient>
)

data class ReplaceableIngredient(
    val recipeIngredient: String,
    val userIngredient: String
)