package com.example.mumuk.data.model.mypage

data class RecentRecipe(
    val recipeId: Long,
    val name: String,
    val image: String?,
    var liked: Boolean
)