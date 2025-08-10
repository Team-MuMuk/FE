package com.example.mumuk.data.model.mypage

data class RecentRecipe(
    val name: String,
    val image: String,
    var liked: Boolean,
    val recipeId: Long? = null
)

