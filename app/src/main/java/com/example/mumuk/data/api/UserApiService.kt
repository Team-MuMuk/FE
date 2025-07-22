package com.example.mumuk.data.api

import com.example.mumuk.data.model.auth.CommonResponse
import com.example.mumuk.data.model.mypage.UserProfileResponse
import com.example.mumuk.data.model.mypage.UserProfileUpdateRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

interface UserApiService {
    @GET("/api/users/{id}")
    fun getUserProfile(
        @Path("id") userId: Long
    ): Call<UserProfileResponse>
    @PATCH("/api/users/{id}")
    fun updateUserProfile(
        @Path("id") userId: Long,
        @Body request: UserProfileUpdateRequest
    ): Call<CommonResponse>
}