package com.example.mumuk.data.model.health

data class HealthGoalsResponseData(
    val healthGoalList: List<HealthGoal>
)

data class HealthGoalsResponse(
    val status: String,
    val code: String,
    val message: String,
    val data: HealthGoalsResponseData
)