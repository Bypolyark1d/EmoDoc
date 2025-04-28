package com.example.diplomproject.data

data class StressSurveyResult(
    val userId: String = "",
    val stressLevel: Float = 0.0f,
    val timestamp: Long = System.currentTimeMillis()
)
