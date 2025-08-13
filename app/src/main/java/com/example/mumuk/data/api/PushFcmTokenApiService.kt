package com.example.mumuk.data.api

import com.example.mumuk.data.model.ingredient.PushFcmTokenRequest
import com.example.mumuk.data.model.ingredient.PushFcmTokenResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface PushFcmTokenApiService {
    @POST("/api/notification/FcmToken")
    fun saveFcmToken(@Body request: PushFcmTokenRequest): Call<PushFcmTokenResponse>
}