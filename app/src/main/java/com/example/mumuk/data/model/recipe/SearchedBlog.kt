package com.example.mumuk.data.model.recipe

import com.google.gson.annotations.SerializedName

data class SearchedBlog(
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("link")
    val link: String,
    @SerializedName("blogImageUrl")
    val blogImageUrl: String, // API에서 제공하는 이미지
    var ogImageUrl: String? = null // Jsoup으로 가져올 대표 이미지
)