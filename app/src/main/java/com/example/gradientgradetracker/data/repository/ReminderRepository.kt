package com.example.gradientgradetracker.data.repository

import com.example.gradientgradetracker.data.model.Reminder
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class ReminderRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val remindersCollection = firestore.collection("reminders")

    suspend fun addReminder(reminder: Reminder): Result<Reminder> {
        return try {
            val docRef = remindersCollection.document()
            val newReminder = reminder.copy(id = docRef.id)
            docRef.set(newReminder).await()
            Result.success(newReminder)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateReminder(reminder: Reminder): Result<Reminder> {
        return try {
            remindersCollection.document(reminder.id).set(reminder).await()
            Result.success(reminder)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteReminder(reminderId: String): Result<Unit> {
        return try {
            remindersCollection.document(reminderId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getReminders(userId: String): Flow<List<Reminder>> = flow {
        try {
            val snapshot = remindersCollection
                .whereEqualTo("userId", userId)
                .get()
                .await()
            emit(snapshot.toObjects(Reminder::class.java))
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    fun getUpcomingReminders(userId: String): Flow<List<Reminder>> = flow {
        try {
            val currentTime = System.currentTimeMillis()
            val snapshot = remindersCollection
                .whereEqualTo("userId", userId)
                .whereGreaterThan("date", currentTime)
                .get()
                .await()
            emit(snapshot.toObjects(Reminder::class.java))
        } catch (e: Exception) {
            emit(emptyList())
        }
    }
} 