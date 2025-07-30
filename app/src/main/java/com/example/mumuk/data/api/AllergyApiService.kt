package com.example.mumuk.data.api

import com.example.mumuk.data.model.allergy.AllergyOptionsResponse
import com.example.mumuk.data.model.allergy.ToggleAllergyRequest
import com.example.mumuk.data.model.allergy.ToggleAllergyResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT

interface AllergyApiService {
    @GET("/api/allergies")
    fun getAllergyOptions(): Call<AllergyOptionsResponse>

    @PUT("/api/allergies")
    fun toggleAllergies(@Body req: ToggleAllergyRequest): Call<ToggleAllergyResponse>
}