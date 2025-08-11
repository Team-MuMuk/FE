package com.example.mumuk.data.api

import com.example.mumuk.data.model.auth.CommonResponse
import com.example.mumuk.data.model.mypage.RecentViewRequest
import com.example.mumuk.data.model.mypage.UserProfileResponse
import com.example.mumuk.data.model.mypage.UserProfileUpdateRequest
import com.example.mumuk.data.model.search.RecentRecipeResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST

interface UserApiService {
    @GET("/api/user/profile")
    fun getUserProfile(): Call<UserProfileResponse>
    @PATCH("/api/user/profile")
    fun updateUserProfile(@Body req: UserProfileUpdateRequest): Call<CommonResponse>
    @GET("/api/user-recipe/recent-recipe")
    fun getRecentRecipes(): Call<RecentRecipeResponse>
    @POST("/api/user-recipe/recent-recipe")
    fun addRecentRecipe(@Body req: RecentViewRequest): Call<CommonResponse>
}
