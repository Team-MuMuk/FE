package com.example.mumuk.data.model.search

data class SuggestKeywordResponse(
    val status: String,
    val code: String,
    val message: String,
    val data: List<String>?
)