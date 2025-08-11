package com.example.mumuk.data.model.search

import com.google.gson.annotations.SerializedName

data class RecentRecipe(
    @SerializedName("recipeId")
    val id: Long,
    @SerializedName("name")
    val title: String,
    @SerializedName("imageUrl")
    val imageUrl: String?,
    @SerializedName("liked")
    var liked: Boolean
)
