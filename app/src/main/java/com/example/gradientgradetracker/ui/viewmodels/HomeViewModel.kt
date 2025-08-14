package com.example.gradientgradetracker.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gradientgradetracker.data.model.Reminder
import com.example.gradientgradetracker.data.model.ReminderType
import com.example.gradientgradetracker.data.model.Subject
import com.example.gradientgradetracker.data.repository.ReminderRepository
import com.example.gradientgradetracker.data.repository.SubjectRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val subjectRepository = SubjectRepository()
    private val reminderRepository = ReminderRepository()

    private val _subjects = MutableStateFlow<List<Subject>>(emptyList())
    val subjects: StateFlow<List<Subject>> = _subjects.asStateFlow()

    private val _reminders = MutableStateFlow<List<Reminder>>(emptyList())
    val reminders: StateFlow<List<Reminder>> = _reminders.asStateFlow()

    fun loadData(userId: String) {
        viewModelScope.launch {
            subjectRepository.getSubjects(userId)
                .collect { subjects ->
                    _subjects.value = subjects
                }
        }

        viewModelScope.launch {
            reminderRepository.getUpcomingReminders(userId)
                .collect { reminders ->
                    _reminders.value = reminders
                }
        }
    }

    fun addSubject(userId: String, name: String, code: String, targetGrade: Double) {
        viewModelScope.launch {
            val subject = Subject(
                name = name,
                code = code,
                targetGrade = targetGrade,
                userId = userId
            )
            subjectRepository.addSubject(subject)
        }
    }

    fun updateGrade(subjectId: String, gradeType: String, grade: Double) {
        viewModelScope.launch {
            subjectRepository.updateGrade(subjectId, gradeType, grade)
        }
    }

    fun deleteSubject(subjectId: String) {
        viewModelScope.launch {
            subjectRepository.deleteSubject(subjectId)
        }
    }

    fun addReminder(
        userId: String,
        subjectId: String,
        title: String,
        description: String,
        date: Long,
        type: String
    ) {
        viewModelScope.launch {
            val reminder = Reminder(
                subjectId = subjectId,
                title = title,
                description = description,
                date = date,
                type = ReminderType.valueOf(type),
                userId = userId
            )
            reminderRepository.addReminder(userId, reminder)
        }
    }

    fun deleteReminder(userId: String, reminderId: String) {
        viewModelScope.launch {
            reminderRepository.deleteReminder(userId, reminderId)
        }
    }
} 