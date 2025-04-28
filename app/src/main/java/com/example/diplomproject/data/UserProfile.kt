package com.example.diplomproject.data

data class UserProfile(
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val countEntry: Int = 0,
    val currentEmotion: Int = -1,
    val stressLevel: Float = -1.0f
)
