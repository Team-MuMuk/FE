package com.example.mumuk.data.model.auth

data class NaverLoginResponse(
    val status: String,
    val code: String,
    val message: String,
    val data: NaverUserData
)

data class NaverUserData(
    val email: String,
    val nickName: String,
    val profileImage: String,
    val refreshToken: String
)