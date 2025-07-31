package com.example.mumuk.data.repository

import com.example.mumuk.data.model.Recipe
import com.example.mumuk.R

class HealthAiRepository {
    fun getAiRecipes(): List<Recipe> {
        return listOf(
            Recipe(1, R.drawable.bg_mosaic, "연어샐러드"),
            Recipe(2, R.drawable.bg_mosaic, "닭가슴살구이"),
            Recipe(3, R.drawable.bg_mosaic, "두부스테이크"),
            Recipe(4, R.drawable.bg_mosaic, "오트밀죽"),
            Recipe(5, R.drawable.bg_mosaic, "현미밥도시락"),
            Recipe(6, R.drawable.bg_mosaic, "채소스튜"),
            Recipe(7, R.drawable.bg_mosaic, "칼슘두부무침"),
            Recipe(8, R.drawable.bg_mosaic, "단백질볼"),
            Recipe(9, R.drawable.bg_mosaic, "시금치프리타타")
        )
    }
}