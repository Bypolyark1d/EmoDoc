package com.example.diplomproject.data

data class EmotionEntry(
    val userId: String = "",
    val text: String = "",
    val emotion: Int = -1,
    val timestamp: Long = System.currentTimeMillis()
)
