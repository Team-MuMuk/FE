package com.example.mumuk.data.repository

import com.example.mumuk.data.model.Recipe
import com.example.mumuk.R

class IngredientAiRecipeRepository {
    fun getAiRecipes(): List<Recipe> {
        return listOf(
            Recipe(1, R.drawable.bg_mosaic, "두부유부초밥"),
            Recipe(2, R.drawable.bg_mosaic, "버섯소고기볶음"),
            Recipe(3, R.drawable.bg_mosaic, "닭가슴살샐러드"),
            Recipe(4, R.drawable.bg_mosaic, "두부유부초밥"),
            Recipe(5, R.drawable.bg_mosaic, "버섯소고기볶음"),
            Recipe(6, R.drawable.bg_mosaic, "닭가슴살샐러드"),
            Recipe(7, R.drawable.bg_mosaic, "두부유부초밥"),
            Recipe(8, R.drawable.bg_mosaic, "버섯소고기볶음"),
            Recipe(9, R.drawable.bg_mosaic, "닭가슴살샐러드")
        )
    }
}