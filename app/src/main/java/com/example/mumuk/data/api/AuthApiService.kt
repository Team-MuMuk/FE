package com.example.mumuk.data.api

import com.example.mumuk.data.model.auth.LoginRequest
import com.example.mumuk.data.model.auth.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("/api/auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>
}