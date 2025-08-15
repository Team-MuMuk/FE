package com.example.mumuk.data.model

data class RecipeRank(
    val recipeId: Int?,
    val img: Int?,
    val imageUrl: String?,
    val name: String,
    val kcal: Int,
    val rank: Int,
    var isLiked: Boolean = false
)