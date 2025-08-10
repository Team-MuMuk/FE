package com.example.mumuk.ui.mypage

data class RecentRecipe(
    val name: String,
    val image: String,
    var liked: Boolean,
    val recipeId: Long? = null
)

