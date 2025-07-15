package com.example.mumuk.ui.home

import com.example.mumuk.R
import com.example.mumuk.data.model.Recipe

class HomeRecipeRepository {
    fun getTodayRecipes(): List<Recipe> {
        return listOf(
            Recipe(R.drawable.bg_mosaic, "연어 포케", false),
            Recipe(R.drawable.bg_mosaic, "닭가슴살 덮밥", true),
            Recipe(R.drawable.bg_mosaic, "두부유부초밥", false)
        )
    }

    fun getRecentRecipes(): List<Recipe> {
        return listOf(
            Recipe(R.drawable.bg_mosaic, "키토김밥", false),
            Recipe(R.drawable.bg_mosaic, "아보카도 샐러드", true)
        )
    }

    fun getHealthRecipes(): List<Recipe> {
        return listOf(
            Recipe(R.drawable.bg_mosaic, "샐러드 파스타", true),
            Recipe(R.drawable.bg_mosaic, "단호박 에그슬럿", false)
        )
    }

    fun getRecommendedRecipes(): List<Recipe> {
        return listOf(
            Recipe(R.drawable.bg_mosaic, "토마토 스튜", false),
            Recipe(R.drawable.bg_mosaic, "버섯 크림 리조또", true),
            Recipe(R.drawable.bg_mosaic, "가지 덮밥", false)
        )
    }
}