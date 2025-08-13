package com.example.mumuk.data.model.bookmark

data class LikedRecipePage(
    val userId: Long,
    val likedRecipes: List<LikedRecipeItem>,
    val currentPage: Int,
    val totalPages: Int,
    val totalElements: Int,
    val pageSize: Int,
    val hasNext: Boolean
)

data class LikedRecipeItem(
    val recipeId: Long,
    val name: String,
    val imageUrl: String?,
    val liked: Boolean
)

data class LikedRecipeResponse(
    val status: String,
    val code: String,
    val message: String,
    val data: LikedRecipePage
)
