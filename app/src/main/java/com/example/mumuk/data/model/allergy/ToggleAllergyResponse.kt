package com.example.mumuk.data.model.allergy

data class ToggleAllergyResult(
    val allergyType: String,
    val action: String
)

data class ToggleAllergyResponseData(
    val allergyOptions: List<AllergyOption>
)

data class ToggleAllergyResponse(
    val status: String,
    val code: String,
    val message: String,
    val data: ToggleAllergyResponseData
)