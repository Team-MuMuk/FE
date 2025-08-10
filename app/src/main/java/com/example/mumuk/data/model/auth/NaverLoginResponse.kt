package com.example.mumuk.data.model.auth

import com.google.gson.annotations.SerializedName

data class NaverLoginResponse(
    @SerializedName("status") val status: String,
    @SerializedName("code") val code: String?,
    @SerializedName("message") val message: String?,
    @SerializedName("data") val data: NaverLoginData?
)

data class NaverLoginData(
    @SerializedName("email") val email: String,
    @SerializedName("nickName") val nickName: String,
    @SerializedName("profileImage") val profileImage: String,
    @SerializedName("accessToken") val accessToken: String,
    @SerializedName("refreshToken") val refreshToken: String
)