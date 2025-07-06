package com.example.mumuk.data.model

data class Recipe(
    val imageResId: Int,
    val title: String,
    val subtitle: String,
    val isLiked: Boolean = false
)