package com.example.mumuk.data.api


import com.example.mumuk.data.model.auth.CommonResponse
import com.example.mumuk.data.model.auth.FindIdRequest
import com.example.mumuk.data.model.auth.FindPwRequest
import com.example.mumuk.data.model.auth.LoginRequest
import com.example.mumuk.data.model.auth.LoginResponse
import com.example.mumuk.data.model.auth.ReissuePwRequest
import com.example.mumuk.data.model.auth.ReissuePwResponse
import com.example.mumuk.data.model.auth.SignupRequest
import com.example.mumuk.data.model.auth.SignupResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.PATCH
import retrofit2.http.POST

interface AuthApiService {
    @POST("/api/auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>
    @POST("/api/auth/sign-up")
    fun signUp(@Body request: SignupRequest): Call<SignupResponse>
    @PATCH("/api/auth/reissue-pw")
    fun reissuePassword(@Body request: ReissuePwRequest): Call<ReissuePwResponse>
    @PATCH("/api/auth/logout")
    fun logout(
        @retrofit2.http.Header("X-Refresh-Token") refreshToken: String,
        @retrofit2.http.Header("X-Login-Type") loginType: String = "LOCAL"
    ): Call<CommonResponse>
    @PATCH("/api/auth/find-pw")
    fun findPassword(@Body request: FindPwRequest): Call<CommonResponse>
    @PATCH("/api/auth/find-id")
    fun findId(@Body request: FindIdRequest): Call<CommonResponse>
    @DELETE("/api/auth/withdraw")
    fun withdraw(): Call<CommonResponse>

}