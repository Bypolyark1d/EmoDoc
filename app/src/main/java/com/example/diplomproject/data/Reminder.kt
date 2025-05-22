package com.example.diplomproject.data

import kotlinx.serialization.Serializable

@Serializable
data class Reminder(
    val id: Long = System.currentTimeMillis(),
    val year: Int,
    val month: Int,
    val day: Int,
    val hour: Int,
    val minute: Int
)