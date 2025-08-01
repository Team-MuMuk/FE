package com.example.mumuk.data.repository

import com.example.mumuk.R
import com.example.mumuk.data.model.Recipe

class RecommendRecipeRepository {

    suspend fun getRecommendedRecipes(): List<Recipe> {
        return listOf(
            Recipe(1, R.drawable.img_food_sample, "두부유부초밥"),
            Recipe(2, R.drawable.img_food_sample, "저당 초콜릿 케이크"),
            Recipe(3, R.drawable.img_food_sample, "연어 포케"),
            Recipe(4, R.drawable.img_food_sample, "닭가슴살 샐러드"),
            Recipe(5, R.drawable.img_food_sample, "두부유부초밥"),
            Recipe(6, R.drawable.img_food_sample, "저당 초콜릿 케이크"),
            Recipe(7, R.drawable.img_food_sample, "연어 포케"),
            Recipe(8, R.drawable.img_food_sample, "닭가슴살 샐러드")
        )
    }
}