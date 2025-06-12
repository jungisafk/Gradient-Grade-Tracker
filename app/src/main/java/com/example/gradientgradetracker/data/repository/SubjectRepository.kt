package com.example.gradientgradetracker.data.repository

import com.example.gradientgradetracker.data.model.Subject
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class SubjectRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val subjectsCollection = firestore.collection("subjects")

    suspend fun addSubject(subject: Subject): Result<Subject> {
        return try {
            val docRef = subjectsCollection.document()
            val newSubject = subject.copy(id = docRef.id)
            docRef.set(newSubject).await()
            Result.success(newSubject)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateSubject(subject: Subject): Result<Subject> {
        return try {
            subjectsCollection.document(subject.id).set(subject).await()
            Result.success(subject)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteSubject(subjectId: String): Result<Unit> {
        return try {
            subjectsCollection.document(subjectId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getSubjects(userId: String): Flow<List<Subject>> = flow {
        try {
            val snapshot = subjectsCollection
                .whereEqualTo("userId", userId)
                .get()
                .await()
            emit(snapshot.toObjects(Subject::class.java))
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    suspend fun updateGrade(
        subjectId: String,
        gradeType: String,
        grade: Double
    ): Result<Subject> {
        return try {
            val subject = subjectsCollection.document(subjectId).get().await()
                .toObject(Subject::class.java) ?: throw Exception("Subject not found")
            
            val updatedSubject = when (gradeType) {
                "prelim" -> subject.copy(prelimGrade = grade)
                "midterm" -> subject.copy(midtermGrade = grade)
                "final" -> subject.copy(finalGrade = grade)
                else -> throw Exception("Invalid grade type")
            }
            
            subjectsCollection.document(subjectId).set(updatedSubject).await()
            Result.success(updatedSubject)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 