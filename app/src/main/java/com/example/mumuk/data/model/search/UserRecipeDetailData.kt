package com.example.mumuk.data.model.search

import com.example.mumuk.data.model.RecipeIngredient

data class UserRecipeDetailData(
    val id: Long,
    val title: String,
    val recipeImage: String,
    val description: String,
    val cookingTime: Int,
    val calories: Int,
    val protein: Int,
    val carbohydrate: Int,
    val fat: Int,
    val categories: List<String>,
    val ingredients: String,
    val sourceUrl: String,
    val recipeIngredients: List<RecipeIngredient>,
    val inFridgeIngredients: List<String>,
    val notInFridgeIngredients: List<String>,
    val viewed: Boolean,
    val viewedAt: String?,
    val liked: Boolean
)