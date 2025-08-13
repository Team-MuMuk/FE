package com.example.mumuk.data.model.recommend

data class OcrResponse(
    val status: String,
    val code: String,
    val message: String,
    val data: Map<String, String>
)