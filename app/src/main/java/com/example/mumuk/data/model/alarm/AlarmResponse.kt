package com.example.mumuk.data.model.alarm

data class AlarmResponse(
    val status: String,
    val code: String,
    val message: String,
    val data: List<AlarmItem>
)