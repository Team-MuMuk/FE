package com.example.mumuk.data.model.auth

data class KakaoLoginResponse(
    val status: String,
    val code: String,
    val message: String,
    val data: KakaoUserData
)
data class KakaoUserData(
    val email: String,
    val nickName: String,
    val profileImage: String,
    val refreshToken: String
)