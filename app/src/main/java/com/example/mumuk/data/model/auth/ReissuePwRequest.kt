package com.example.mumuk.data.model.auth

data class ReissuePwRequest(
    val currentPassWord: String,
    val passWord: String,
    val confirmPassWord: String
)
