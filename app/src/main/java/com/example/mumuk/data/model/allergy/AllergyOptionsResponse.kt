package com.example.mumuk.data.model.allergy

data class AllergyOptionsResponseData(
    val allergyOptions: List<AllergyOption>
)

data class AllergyOptionsResponse(
    val status: String,
    val code: String,
    val message: String,
    val data: AllergyOptionsResponseData
)