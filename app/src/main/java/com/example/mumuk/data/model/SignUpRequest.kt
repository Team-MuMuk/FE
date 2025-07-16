package com.example.mumuk.data.model

data class SignUpRequest(
    val name: String,
    val nickname: String,
    val phoneNumber: String,
    val loginId: String,
    val password: String,
    val confirmPassword: String
)
