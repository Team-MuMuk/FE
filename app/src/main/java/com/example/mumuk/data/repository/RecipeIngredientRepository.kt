package com.example.mumuk.data.repository

import com.example.mumuk.data.model.RecipeIngredient

class RecipeIngredientRepository {
    fun getAllIngredients(): List<RecipeIngredient> {
        return listOf(
            RecipeIngredient("연어", true),
            RecipeIngredient("아보카도", true),
            RecipeIngredient("방울토마토", true),
            RecipeIngredient("올리브", true),
            RecipeIngredient("적양파", true),
            RecipeIngredient("참치", false),
            RecipeIngredient("크림치즈", false),
            RecipeIngredient("연어", true),
            RecipeIngredient("아보카도", true),
            RecipeIngredient("방울토마토", true),
            RecipeIngredient("올리브", true),
            RecipeIngredient("적양파", true),
            RecipeIngredient("참치", false),
            RecipeIngredient("크림치즈", false),
            RecipeIngredient("연어", true),
            RecipeIngredient("아보카도", true),
            RecipeIngredient("방울토마토", true),
            RecipeIngredient("올리브", true),
            RecipeIngredient("적양파", true),
            RecipeIngredient("참치", false),
            RecipeIngredient("크림치즈", false)
        )
    }
}