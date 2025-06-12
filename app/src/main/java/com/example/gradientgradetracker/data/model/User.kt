package com.example.gradientgradetracker.data.model

import com.example.gradientgradetracker.ui.screens.UserRole

data class User(
    val id: String = "",
    val email: String = "",
    val role: UserRole = UserRole.STUDENT,
    val createdAt: Long = System.currentTimeMillis()
) 