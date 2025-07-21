package com.example.mumuk.data.model.auth

data class CommonResponse(
    val status: String,
    val code: String,
    val message: String,
    val data: String?
)
