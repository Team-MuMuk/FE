package com.example.mumuk.data.model.bookmark

data class LikedRecipesResponse(
    val status: String,
    val code: String,
    val message: String,
    val data: LikedRecipesPageDto?
)

data class LikedRecipesPageDto(
    val userId: Long,
    val likedRecipes: List<LikedRecipeDto>,
    val currentPage: Int,
    val totalPages: Int,
    val totalElements: Int,
    val pageSize: Int,
    val hasNext: Boolean
)

data class LikedRecipeDto(
    val recipeId: Long,
    val name: String,
    val imageUrl: String?,
    val liked: Boolean
)