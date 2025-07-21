package com.example.mumuk.data.repository

import com.example.mumuk.data.model.Ingredient

class IngredientRepository {
    fun getIngredients(): List<Ingredient> {
        return listOf(
            Ingredient("아보카도 샐러드", "2025-07-23"),
            Ingredient("토마토", "2025-07-22"),
            Ingredient("계란", "2025-07-24"),
            Ingredient("아보카도 샐러드", "2023-10-30"),
            Ingredient("토마토", "2023-11-05"),
            Ingredient("계란", "2023-12-01")
        )
    }
}