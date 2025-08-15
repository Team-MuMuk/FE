package com.example.mumuk.data.model.userinfo

data class PatchUserInfoResponse(
    val status: String,
    val code: String,
    val message: String,
    val data: UserInfoData?
) {
    data class UserInfoData(
        val gender: String?,
        val height: Int?,
        val weight: Int?
    )
}