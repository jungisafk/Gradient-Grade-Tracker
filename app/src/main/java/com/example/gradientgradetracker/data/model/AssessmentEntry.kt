package com.example.gradientgradetracker.data.model

// Represents a single assessment entry for a subject/period
 data class AssessmentEntry(
    val name: String,
    val scoreObtained: Double,
    val totalScore: Double,
    val weight: Double,
    val date: String,
    val assessmentType: String
)

// Represents the overview of a subject for dashboard/analytics
 data class SubjectOverview(
    val id: String,
    val name: String,
    val currentGrade: Double,
    val prelim: Double,
    val midterm: Double,
    val final: Double,
    val status: SubjectStatus,
    val assessments: Map<String, List<AssessmentEntry>>
)

enum class SubjectStatus {
    ALERT, ON_TRACK
} 