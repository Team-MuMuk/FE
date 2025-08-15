package com.example.mumuk.data.model.health

data class ToggleHealthGoalResponseData(
    val healthGoalList: List<HealthGoal>
)

data class ToggleHealthGoalResponse(
    val status: String,
    val code: String,
    val message: String,
    val data: ToggleHealthGoalResponseData
)