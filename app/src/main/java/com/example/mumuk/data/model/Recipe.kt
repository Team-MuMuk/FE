package com.example.mumuk.data.model

data class Recipe(
    val id: Long,
    val img: Int?,
    val title: String,
    var isLiked: Boolean = false,
    val recipeImageUrl: String? = null
)