package com.example.mumuk.data.repository

import com.example.mumuk.R
import com.example.mumuk.data.model.Recipe

class BookmarkRecipeRepository {
    suspend fun getBookmarkedRecipes(): List<Recipe> {
        return listOf(
            Recipe(R.drawable.img_food_sample, "두부유부초밥"),
            Recipe(null, "저당 초콜릿 케이크"),
            Recipe(null, "연어 포케"),
            Recipe(R.drawable.img_food_sample, "닭가슴살 샐러드"),
            Recipe(null, "두부유부초밥"),
            Recipe(null, "저당 초콜릿 케이크"),
            Recipe(null, "연어 포케"),
            Recipe(null, "닭가슴살 샐러드")
        )
    }
}