package com.example.mumuk.data.repository

import android.content.Context
import android.util.Log
import com.example.mumuk.data.api.RetrofitClient
import com.example.mumuk.data.model.search.UserRecipeDetailResponse
import retrofit2.Response

class UserRecipeRepository(private val context: Context) {
    private val api = RetrofitClient.getUserRecipeApi(context)

    suspend fun getUserRecipeDetail(recipeId: Long): Response<UserRecipeDetailResponse> {
        Log.d("UserRecipeRepository", "getUserRecipeDetail() called with recipeId: $recipeId")
        val response = api.getUserRecipeDetail(recipeId)
        Log.d("UserRecipeRepository", "API response: isSuccessful=${response.isSuccessful}, code=${response.code()}")
        return response
    }
}