package com.example.gradientgradetracker.data.model

data class Reminder(
    val id: String = "",
    val subjectId: String = "",
    val title: String = "",
    val description: String = "",
    val date: Long = 0,
    val type: ReminderType = ReminderType.QUIZ,
    val userId: String = ""
    ) 