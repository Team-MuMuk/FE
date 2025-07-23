package com.example.mumuk.data.model.mypage

data class UserProfileUpdateRequest(
    val name: String,
    val nickName: String,
    val profileImage: String,
    val statusMessage: String
)
