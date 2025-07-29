package com.example.mumuk.data.repository

import com.example.mumuk.data.model.Recipe
import com.example.mumuk.R

class HealthAiRepository {
    fun getAiRecipes(): List<Recipe> {
        return listOf(
            Recipe(img = R.drawable.bg_mosaic, title = "연어샐러드"),
            Recipe(img = R.drawable.bg_mosaic, title = "닭가슴살구이"),
            Recipe(img = R.drawable.bg_mosaic, title = "두부스테이크"),
            Recipe(img = R.drawable.bg_mosaic, title = "오트밀죽"),
            Recipe(img = R.drawable.bg_mosaic, title = "현미밥도시락"),
            Recipe(img = R.drawable.bg_mosaic, title = "채소스튜"),
            Recipe(img = R.drawable.bg_mosaic, title = "칼슘두부무침"),
            Recipe(img = R.drawable.bg_mosaic, title = "단백질볼"),
            Recipe(img = R.drawable.bg_mosaic, title = "시금치프리타타")
        )
    }
}