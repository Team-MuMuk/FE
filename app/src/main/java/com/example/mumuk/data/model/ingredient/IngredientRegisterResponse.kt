package com.example.mumuk.data.model.ingredient

data class IngredientRegisterResponse(
    val status: String,
    val code: String,
    val message: String,
    val data: String?
)