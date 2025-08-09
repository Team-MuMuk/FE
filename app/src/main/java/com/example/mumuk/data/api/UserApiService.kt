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
    @GET("/api/user/profile")
    fun getUserProfile(): Call<UserProfileResponse>
    @PATCH("/api/user/profile")
    fun updateUserProfile(
        @Body request: UserProfileUpdateRequest
    ): Call<CommonResponse>
}