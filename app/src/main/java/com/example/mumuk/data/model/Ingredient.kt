package com.example.mumuk.data.model

import java.io.Serializable

data class Ingredient(
    val name: String,
    val expiryDate: String
) : Serializable