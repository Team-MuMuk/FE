package com.example.mumuk.ui.home

import com.example.mumuk.R
import com.example.mumuk.data.model.Recipe

class HomeRecipeRepository {
    fun getTodayRecipes(): List<Recipe> {
        return listOf(
            Recipe(1, R.drawable.bg_mosaic, "연어 포케", false),
            Recipe(2, R.drawable.bg_mosaic, "닭가슴살 덮밥", true),
            Recipe(3, R.drawable.bg_mosaic, "두부유부초밥", false),
            Recipe(4, R.drawable.bg_mosaic, "연어 포케", false),
            Recipe(5, R.drawable.bg_mosaic, "닭가슴살 덮밥", true),
            Recipe(6, R.drawable.bg_mosaic, "두부유부초밥", false)
        )
    }

    fun getRecentRecipes(): List<Recipe> {
        return listOf(
            Recipe(7, R.drawable.bg_mosaic, "키토김밥", false),
            Recipe(8, R.drawable.bg_mosaic, "아보카도 샐러드", true)
        )
    }
}