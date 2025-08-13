package com.example.mumuk.data.api

import com.example.mumuk.data.model.recommend.BaseResponse
import com.example.mumuk.data.model.recommend.OcrResponse
import com.example.mumuk.data.model.recommend.RecommendRecipeResponse
import okhttp3.MultipartBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface RecipeApiService {
    @GET("/api/v1/recipe/recommend/ingredient")
    suspend fun getRecommendIngredientRecipes(): BaseResponse<List<RecommendRecipeResponse>>

    @GET("/api/v1/recipe/recommend/combined")
    suspend fun getRecommendCombinedRecipes(): BaseResponse<List<RecommendRecipeResponse>>

    @Multipart
    @POST("/api/health/ocr")
    suspend fun postOcrImage(
        @Part image: MultipartBody.Part
    ): OcrResponse
}