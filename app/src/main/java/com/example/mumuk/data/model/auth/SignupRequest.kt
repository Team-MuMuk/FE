package com.example.mumuk.data.model.auth

data class SignupRequest(
    val name: String,
    val nickname: String,
    val phoneNumber: String,
    val loginId: String,
    val password: String,
    val confirmPassword: String
)
