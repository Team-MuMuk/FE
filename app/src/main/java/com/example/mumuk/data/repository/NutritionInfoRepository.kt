package com.example.mumuk.data.repository

import com.example.mumuk.data.model.NutritionInfo

class NutritionInfoRepository {
    suspend fun getNutritionInfoList(): List<NutritionInfo> {
        return listOf(
            NutritionInfo("단백질", "40g"),
            NutritionInfo("탄수화물", "25g"),
            NutritionInfo("지방", "15g"),
            NutritionInfo("칼로리", "350kcal"),
            NutritionInfo("나트륨", "300mg"),
            NutritionInfo("당류", "5g")
        )
    }
}