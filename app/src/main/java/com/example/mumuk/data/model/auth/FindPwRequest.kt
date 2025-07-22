package com.example.mumuk.data.model.auth

data class FindPwRequest(
    val loginId: String,
    val name: String,
    val phoneNumber: String
)
