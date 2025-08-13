package com.example.mumuk.data.api

import com.example.mumuk.data.model.ingredient.PushAgreeRequest
import com.example.mumuk.data.model.ingredient.PushAgreeResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface PushAlarmApiService {
    @POST("/api/notification/pushAgree")
    fun pushAgree(@Body request: PushAgreeRequest): Call<PushAgreeResponse>
}