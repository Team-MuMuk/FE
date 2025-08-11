package com.example.mumuk.data.model.search

import com.google.gson.annotations.SerializedName

data class PopularKeywordData(
    @SerializedName("trendRecipeTitleList")
    val trendRecipeTitleList: List<String>?,
    @SerializedName("localDateTime")
    val localDateTime: String?
)