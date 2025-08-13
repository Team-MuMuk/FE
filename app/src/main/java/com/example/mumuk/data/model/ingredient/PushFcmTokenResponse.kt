package com.example.mumuk.data.model.ingredient

data class PushFcmTokenResponse(
    val status: String,
    val code: String,
    val message: String,
    val data: Int
)