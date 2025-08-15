package com.example.mumuk.data.api

import com.example.mumuk.data.model.health.HealthGoalsResponse
import retrofit2.Call
import retrofit2.http.GET

interface HealthApiService {
    @GET("/api/health-goals")
    fun getHealthGoals(): Call<HealthGoalsResponse>
}