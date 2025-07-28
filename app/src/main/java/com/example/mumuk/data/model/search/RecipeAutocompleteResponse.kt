package com.example.mumuk.data.model.search

data class RecipeAutocompleteResponse(
    val status: String?,
    val code: String?,
    val message: String?,
    val data: List<String>?
)