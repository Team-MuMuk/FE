package com.example.mumuk.data.model.auth

data class LoginResponse(
    val status: String,
    val code: String,
    val message: String,
    val data: TokenData?
)

data class TokenData(
    val accessToken: String,
    val refreshToken: String
)