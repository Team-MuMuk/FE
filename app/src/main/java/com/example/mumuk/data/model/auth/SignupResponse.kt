package com.example.mumuk.data.model.auth

data class SignupResponse(
    val status: String,
    val code: String,
    val message: String,
    val data: String
)