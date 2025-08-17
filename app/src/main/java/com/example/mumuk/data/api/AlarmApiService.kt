package com.example.mumuk.data.api

import com.example.mumuk.data.model.alarm.AlarmResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface AlarmApiService {
    @GET("/api/notification/recent-alarm")
    fun getRecentAlarms(
        @Query("size") size: Int = 200
    ): Call<AlarmResponse>
}