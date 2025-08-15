package com.example.mumuk.data.api

import com.example.mumuk.data.model.health.HealthGoalsResponse
import com.example.mumuk.data.model.health.ToggleHealthGoalRequest
import com.example.mumuk.data.model.health.ToggleHealthGoalResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT

interface HealthApiService {
    @GET("/api/health-goals")
    fun getHealthGoals(): Call<HealthGoalsResponse>

    @PUT("/api/health-goals")
    fun toggleHealthGoals(@Body req: ToggleHealthGoalRequest): Call<ToggleHealthGoalResponse>
}