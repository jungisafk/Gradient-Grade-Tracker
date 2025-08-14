package com.example.gradientgradetracker.data.model

data class Subject(
    val id: String = "",
    val name: String = "",
    val code: String = "",
    val targetGrade: Double = 0.0,
    val prelimGrade: Double? = null,
    val midtermGrade: Double? = null,
    val finalGrade: Double? = null,
    val userId: String = ""
)