package com.example.mumuk.data.model.search

data class RecentRecipe(
    val id: Long,
    val title: String,
    val imageUrl: String?,
    var liked: Boolean
)