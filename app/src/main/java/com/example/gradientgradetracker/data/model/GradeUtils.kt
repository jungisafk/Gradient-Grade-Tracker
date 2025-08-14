package com.example.gradientgradetracker.data.model

fun computePeriodGrade(assessments: List<AssessmentEntry>): Double {
    if (assessments.isEmpty()) return 0.0
    var totalWeighted = 0.0
    var totalWeight = 0.0
    for (entry in assessments) {
        totalWeighted += (entry.scoreObtained / entry.totalScore) * entry.weight
        totalWeight += entry.weight
    }
    return if (totalWeight > 0) (totalWeighted / totalWeight) * 5.0 else 0.0 // Assuming 5-point scale
} 