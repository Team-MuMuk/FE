package com.example.mumuk.data.model.mypage

data class UserProfileResponse(
    val status: String,
    val code: String,
    val message: String,
    val data: UserProfileData
)
data class UserProfileData(
    val name: String,
    val nickName: String,
    val profileImage: String,
    val statusMessage: String
)