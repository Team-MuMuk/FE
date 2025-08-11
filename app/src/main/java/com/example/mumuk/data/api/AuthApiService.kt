package com.example.mumuk.data.api


import com.example.mumuk.data.model.auth.CheckCurrentPwRequest
import com.example.mumuk.data.model.auth.CommonResponse
import com.example.mumuk.data.model.auth.FindIdRequest
import com.example.mumuk.data.model.auth.FindPwRequest
import com.example.mumuk.data.model.auth.KakaoLoginResponse
import com.example.mumuk.data.model.auth.LoginRequest
import com.example.mumuk.data.model.auth.LoginResponse
import com.example.mumuk.data.model.auth.NaverLoginResponse
import com.example.mumuk.data.model.auth.ReissuePwRequest
import com.example.mumuk.data.model.auth.SignupRequest
import com.example.mumuk.data.model.auth.SignupResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthApiService {
    @POST("/api/auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("/api/auth/sign-up")
    fun signUp(@Body request: SignupRequest): Call<SignupResponse>

    @POST("/api/auth/check-current-pw")
    fun checkCurrentPassword(@Body request: CheckCurrentPwRequest): Call<CommonResponse>

    @PATCH("/api/auth/reissue-pw")
    fun reissuePassword(@Body request: ReissuePwRequest): Call<CommonResponse>

    @PATCH("/api/auth/logout")
    fun logout(
        @Header("X-Refresh-Token") refreshToken: String,
        @Header("X-Login-Type") loginType: String
    ): Call<CommonResponse>

    @PATCH("/api/auth/find-pw")
    fun findPassword(@Body request: FindPwRequest): Call<CommonResponse>

    @PATCH("/api/auth/find-id")
    fun findId(@Body request: FindIdRequest): Call<CommonResponse>

    @DELETE("/api/auth/withdraw")
    fun withdraw(): Call<CommonResponse>

    @GET("/api/auth/kakao-login")
    fun kakaoLogin(
        @Query("access_token") accessToken: String,
        @Query("state") state: String
    ): Call<KakaoLoginResponse>

    @GET("/api/auth/naver-login")
    fun naverLogin(
        @Query("access_token") accessToken: String,
        @Query("state") state: String
    ): Call<NaverLoginResponse>

    @POST("/api/auth/reissue")
    suspend fun reissueToken(
        @Header("X-Refresh-Token") refreshToken: String,
        @Header("X-Login-Type") loginType: String
    ): Response<LoginResponse>

    @GET("/api/auth/exists/nickname")
    fun checkNicknameExists(@Query("value") nickname: String): Call<CommonResponse>

    @GET("/api/auth/exists/phone-number")
    fun checkPhoneNumberExists(@Query("value") phoneNumber: String): Call<CommonResponse>

    @GET("/api/auth/exists/login-id")
    fun checkLoginIdExists(@Query("value") loginId: String): Call<CommonResponse>



}