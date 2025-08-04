package com.example.mumuk.data.model

import com.google.gson.annotations.SerializedName

data class RecipeIngredient(
    val name: String,
    @SerializedName("inFridge")
    val isAvailable: Boolean
)