package com.example.mumuk.data.model.mypage

data class RecentRecipeResponse(
    val recipeId: Long,
    val name: String,
    val imageUrl: String,
    val liked: Boolean
)

data class RecentRecipeListResponse(
    val status: String,
    val code: String,
    val message: String,
    val data: RecentRecipeData
)

data class RecentRecipeData(
    val recentRecipes: List<RecentRecipeResponse>
)
