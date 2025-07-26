package com.example.mumuk.data.repository

import com.example.mumuk.R
import com.example.mumuk.data.model.RecipeRank

class RecipeRankRepository {
    fun getHealthRankRecipes(): List<RecipeRank> {
        return listOf(
            RecipeRank(R.drawable.bg_mosaic, "헬스 레시피 1", 321, 1),
            RecipeRank(R.drawable.bg_mosaic, "헬스 레시피 2", 250, 2),
            RecipeRank(R.drawable.bg_mosaic, "헬스 레시피 3", 180, 3),
            RecipeRank(R.drawable.bg_mosaic, "헬스 레시피 4", 210, 4),
            RecipeRank(R.drawable.bg_mosaic, "헬스 레시피 5", 195, 5)
        )
    }

    fun getIngredientRankRecipes(): List<RecipeRank> {
        return listOf(
            RecipeRank(R.drawable.bg_mosaic, "재료 레시피 1", 234, 1),
            RecipeRank(R.drawable.bg_mosaic, "재료 레시피 2", 155, 2),
            RecipeRank(R.drawable.bg_mosaic, "재료 레시피 3", 390, 3),
            RecipeRank(R.drawable.bg_mosaic, "재료 레시피 4", 210, 4),
            RecipeRank(R.drawable.bg_mosaic, "재료 레시피 5", 120, 5)
        )
    }

    fun getRecentRankRecipes(): List<RecipeRank> {
        return listOf(
            RecipeRank(R.drawable.bg_mosaic, "최근 본 레시피 1", 402, 1),
            RecipeRank(R.drawable.bg_mosaic, "최근 본 레시피 2", 133, 2),
            RecipeRank(R.drawable.bg_mosaic, "최근 본 레시피 3", 288, 3),
            RecipeRank(R.drawable.bg_mosaic, "최근 본 레시피 4", 175, 4),
            RecipeRank(R.drawable.bg_mosaic, "최근 본 레시피 5", 222, 5)
        )
    }
}