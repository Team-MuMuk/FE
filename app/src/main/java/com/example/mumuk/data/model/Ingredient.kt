package com.example.mumuk.data.model

import java.io.Serializable

data class Ingredient(
    val id: Int,
    val name: String,
    val expiryDate: String,
    var count: Int = 1
) : Serializable