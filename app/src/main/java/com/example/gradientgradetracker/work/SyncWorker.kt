package com.example.gradientgradetracker.work

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.gradientgradetracker.data.local.AppDb
import com.example.gradientgradetracker.data.repository.GradesRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SyncWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val context = applicationContext
        val db = androidx.room.Room.databaseBuilder(context, AppDb::class.java, "grades.db")
            .addMigrations(com.example.gradientgradetracker.data.local.DbMigrations.MIGRATION_1_2)
            .build()
        val repo = GradesRepository(db, FirebaseFirestore.getInstance())
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@withContext Result.success()
        val pending = repo.getPending()
        val pendingSubjects = repo.getPendingSubjects()

        pending.forEach { a ->
            when (a.pendingOp) {
                "insert", "update" -> {
                    val ref = FirebaseFirestore.getInstance().collection("users")
                        .document(userId)
                        .collection("assessments")
                    val doc = if (a.remoteId == null) ref.document() else ref.document(a.remoteId)
                    doc.set(
                        mapOf(
                            "localId" to a.localId,
                            "subjectId" to a.subjectId,
                            "period" to a.period,
                            "type" to a.type,
                            "score" to a.score,
                            "total" to a.total,
                            "weight" to a.weight,
                            "date" to a.date,
                            "updatedAt" to FieldValue.serverTimestamp()
                        ),
                        SetOptions.merge()
                    ).await()
                    repo.markSynced(a.localId, doc.id)
                }
                "delete" -> {
                    val id = a.remoteId ?: return@forEach
                    FirebaseFirestore.getInstance().collection("users")
                        .document(userId)
                        .collection("assessments").document(id).delete().await()
                    repo.markSynced(a.localId, id)
                }
            }
        }

        // Sync subjects
        pendingSubjects.forEach { s ->
            val ref = FirebaseFirestore.getInstance().collection("users").document(userId).collection("subjects").document(s.id)
            ref.set(
                mapOf(
                    "name" to s.name,
                    "icon" to s.icon,
                    "updatedAt" to FieldValue.serverTimestamp()
                ), SetOptions.merge()
            ).await()
            repo.markSubjectSynced(s.id)
        }

        Result.success()
    }

    companion object {
        fun enqueue(context: Context) {
            val req = OneTimeWorkRequestBuilder<SyncWorker>()
                .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                .build()
            WorkManager.getInstance(context).enqueueUniqueWork("grades_sync", ExistingWorkPolicy.APPEND, req)
        }
    }
}


