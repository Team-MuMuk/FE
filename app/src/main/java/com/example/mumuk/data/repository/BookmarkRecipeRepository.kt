package com.example.mumuk.data.repository

import com.example.mumuk.R
import com.example.mumuk.data.model.Recipe

class BookmarkRecipeRepository {
    suspend fun getWeightRecipes(): List<Recipe> {
        return listOf(
            Recipe(1, R.drawable.img_food_sample, "체중관리 식단1"),
            Recipe(2, null, "체중관리 식단2"),
            Recipe(3, R.drawable.img_food_sample, "체중관리 식단3")
        )
    }

    suspend fun getHealthRecipes(): List<Recipe> {
        return listOf(
            Recipe(4, null, "건강식단1"),
            Recipe(5, R.drawable.img_food_sample, "건강식단2"),
            Recipe(6, null, "건강식단3")
        )
    }

    suspend fun getRandomRecipes(): List<Recipe> {
        return listOf(
            Recipe(7, null, "랜덤식단1"),
            Recipe(8, null, "랜덤식단2"),
            Recipe(9, R.drawable.img_food_sample, "랜덤식단3")
        )
    }
}