package com.example.mumuk.data.model.alarm

data class AlarmItem(
    val notificationLogid: Long,
    val title: String,
    val body: String,
    val messageId: String,
    val status: String,
    val createdAt: String
)