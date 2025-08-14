package com.example.gradientgradetracker.data.repository

import com.example.gradientgradetracker.data.model.Reminder
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class ReminderRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private fun col(userId: String) = firestore.collection("users").document(userId).collection("reminders")

    suspend fun addReminder(userId: String, reminder: Reminder): Result<Reminder> {
        return try {
            val docRef = col(userId).document()
            val newReminder = reminder.copy(id = docRef.id, userId = userId)
            docRef.set(newReminder).await()
            Result.success(newReminder)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateReminder(userId: String, reminder: Reminder): Result<Reminder> {
        return try {
            col(userId).document(reminder.id).set(reminder.copy(userId = userId)).await()
            Result.success(reminder)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteReminder(userId: String, reminderId: String): Result<Unit> {
        return try {
            col(userId).document(reminderId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getReminders(userId: String): Flow<List<Reminder>> = flow {
        try {
            val snapshot = col(userId).get().await()
            emit(snapshot.toObjects(Reminder::class.java))
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    fun listenReminders(userId: String, onChange: (List<Reminder>) -> Unit): ListenerRegistration {
        return col(userId).addSnapshotListener { snap, _ ->
            if (snap != null) onChange(snap.toObjects(Reminder::class.java))
        }
    }

    fun getUpcomingReminders(userId: String): Flow<List<Reminder>> = flow {
        try {
            val currentTime = System.currentTimeMillis()
            val snapshot = col(userId)
                .whereGreaterThan("date", currentTime)
                .get()
                .await()
            emit(snapshot.toObjects(Reminder::class.java))
        } catch (e: Exception) {
            emit(emptyList())
        }
    }
}