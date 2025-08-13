package com.example.mumuk.data.repository

object CategoryKeys {
    // 체형/체중관리
    const val WEIGHT_LOSS = "weight_loss"
    const val MUSCLE_GAIN = "muscle_gain"

    // 건강관리
    const val SUGAR_REDUCTION = "sugar_reduction"
    const val BLOOD_PRESSURE  = "blood_pressure"
    const val CHOLESTEROL     = "cholesterol"
    const val DIGESTION       = "digestion"

    val WEIGHT_ALL = listOf(WEIGHT_LOSS, MUSCLE_GAIN)
    val HEALTH_ALL = listOf(SUGAR_REDUCTION, BLOOD_PRESSURE, CHOLESTEROL, DIGESTION)
}
