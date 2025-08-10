package com.example.mumuk.data.model.recipe

import com.google.gson.annotations.SerializedName

data class BlogResponse(
    @SerializedName("blogs")
    val blogs: List<SearchedBlog>
)