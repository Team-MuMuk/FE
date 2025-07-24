package com.example.mumuk.data.model.search

data class RecentSearchResponse(
    val status: String?,
    val code: String?,
    val message: String?,
    val data: List<RecentSearch>?
)