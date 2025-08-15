package com.example.mumuk.data.api

import com.example.mumuk.data.model.userinfo.PatchUserInfoRequest
import com.example.mumuk.data.model.userinfo.PatchUserInfoResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.PATCH
import retrofit2.http.Headers

interface UserInfoApiService {
    @PATCH("/api/user-info")
    @Headers("Content-Type: application/json")
    fun patchUserInfo(
        @Body request: PatchUserInfoRequest
    ): Call<PatchUserInfoResponse>
}