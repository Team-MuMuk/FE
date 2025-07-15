package com.example.mumuk.data.model

data class Recipe(
    val img: Int,
    val title: String,
    val isLiked: Boolean = false
)