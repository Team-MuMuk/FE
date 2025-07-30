package com.example.mumuk.data.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH

data class AllergyOption(
    val id: Int,
    val allergyType: String
)

data class AllergyOptionsResponseData(
    val allergyOptions: List<AllergyOption>
)

data class AllergyOptionsResponse(
    val status: String,
    val code: String,
    val message: String,
    val data: AllergyOptionsResponseData
)

data class ToggleAllergyRequest(
    val allergyTypeList: List<String>
)

data class ToggleAllergyResult(
    val allergyType: String,
    val action: String // "ADDED" or "REMOVED"
)

data class ToggleAllergyResponseData(
    val results: List<ToggleAllergyResult>
)

data class ToggleAllergyResponse(
    val status: String,
    val code: String,
    val message: String,
    val data: ToggleAllergyResponseData
)

interface AllergyApiService {
    @GET("/api/allergies")
    fun getAllergyOptions(): Call<AllergyOptionsResponse>

    @PATCH("/api/allergies")
    fun toggleAllergies(@Body req: ToggleAllergyRequest): Call<ToggleAllergyResponse>
}