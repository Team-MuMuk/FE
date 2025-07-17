package com.example.mumuk.data.repository

import com.example.mumuk.R
import com.example.mumuk.data.model.Recipe

class RecommendRecipeRepository {

    suspend fun getRecommendedRecipes(): List<Recipe> {
        return listOf(
            Recipe(R.drawable.img_food_sample, "두부유부초밥"),
            Recipe(R.drawable.img_food_sample, "저당 초콜릿 케이크"),
            Recipe(R.drawable.img_food_sample, "연어 포케"),
            Recipe(R.drawable.img_food_sample, "닭가슴살 샐러드"),
            Recipe(R.drawable.img_food_sample, "두부유부초밥"),
            Recipe(R.drawable.img_food_sample, "저당 초콜릿 케이크"),
            Recipe(R.drawable.img_food_sample, "연어 포케"),
            Recipe(R.drawable.img_food_sample, "닭가슴살 샐러드")
        )
    }
}