package com.example.mumuk.data.model.ingredient

import java.io.Serializable

data class IngredientResponse(
    val status: String,
    val code: String,
    val message: String,
    val data: List<IngredientDto>
)

data class IngredientDto(
    val ingredient_id: Int,
    val name: String,
    val expireDate: String,
    val createdAt: String,
    val quantity: Int
) : Serializable

data class IngredientDeleteResponse(
    val status: String,
    val code: String,
    val message: String,
    val data: String?
)