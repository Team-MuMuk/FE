package com.example.mumuk.data.repository

import com.example.mumuk.data.model.Recipe
import com.example.mumuk.R

class AiRecipeRepository {
    fun getAiRecipes(): List<Recipe> {
        return listOf(
            Recipe(img = R.drawable.bg_mosaic, title = "두부유부초밥"),
            Recipe(img = R.drawable.bg_mosaic, title = "버섯소고기볶음"),
            Recipe(img = R.drawable.bg_mosaic, title = "닭가슴살샐러드"),
            Recipe(img = R.drawable.bg_mosaic, title = "두부유부초밥"),
            Recipe(img = R.drawable.bg_mosaic, title = "버섯소고기볶음"),
            Recipe(img = R.drawable.bg_mosaic, title = "닭가슴살샐러드"),
            Recipe(img = R.drawable.bg_mosaic, title = "두부유부초밥"),
            Recipe(img = R.drawable.bg_mosaic, title = "버섯소고기볶음"),
            Recipe(img = R.drawable.bg_mosaic, title = "닭가슴살샐러드")
        )
    }
}