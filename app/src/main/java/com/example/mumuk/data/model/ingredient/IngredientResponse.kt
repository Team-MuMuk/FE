package com.example.mumuk.data.model.ingredient

data class IngredientResponse(
    val status: String,
    val code: String,
    val message: String,
    val data: List<IngredientDto>
)

data class IngredientDto(
    val name: String,
    val expireDate: String,
    val createdAt: String
)