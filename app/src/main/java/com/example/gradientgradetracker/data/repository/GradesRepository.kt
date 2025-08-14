package com.example.gradientgradetracker.data.repository

import com.example.gradientgradetracker.data.local.AppDb
import com.example.gradientgradetracker.data.local.AssessmentEntity
import com.example.gradientgradetracker.data.local.SubjectEntity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class GradesRepository(
    private val db: AppDb,
    private val firestore: FirebaseFirestore
) {
    val subjectsFlow: Flow<List<SubjectEntity>> = db.subjectDao().observeSubjects()
    fun assessmentsFlow(subjectId: String): Flow<List<AssessmentEntity>> = db.assessmentDao().observeAssessments(subjectId)

    suspend fun upsertSubject(entity: SubjectEntity) = db.subjectDao().upsert(entity)
    suspend fun getPendingSubjects(): List<SubjectEntity> = db.subjectDao().getNeedingRemoteSync()
    suspend fun markSubjectSynced(id: String) = db.subjectDao().markSynced(id, System.currentTimeMillis())

    suspend fun addOrUpdateAssessment(a: AssessmentEntity) {
        db.assessmentDao().upsertAll(listOf(a.copy(
            pendingOp = if (a.remoteId == null) "insert" else "update",
            updatedAt = System.currentTimeMillis()
        )))
    }

    suspend fun markSynced(localId: String, remoteId: String?) = db.assessmentDao().markSynced(localId, remoteId, System.currentTimeMillis())

    suspend fun getPending(): List<AssessmentEntity> = db.assessmentDao().getPending()

    fun startAssessmentsListener(userId: String): ListenerRegistration {
        return firestore.collection("users").document(userId).collection("assessments")
            .addSnapshotListener { snap, _ ->
                if (snap != null) {
                    val now = System.currentTimeMillis()
                    val items = snap.documents.mapNotNull { d ->
                        val subjectId = d.getString("subjectId") ?: return@mapNotNull null
                        AssessmentEntity(
                            localId = d.getString("localId") ?: d.id,
                            remoteId = d.id,
                            subjectId = subjectId,
                            period = d.getString("period") ?: "",
                            type = d.getString("type") ?: "",
                            score = d.getDouble("score") ?: 0.0,
                            total = d.getDouble("total") ?: 0.0,
                            weight = d.getDouble("weight") ?: 0.0,
                            date = d.getString("date") ?: "",
                            updatedAt = now,
                            pendingOp = null
                        )
                    }
                    CoroutineScope(Dispatchers.IO).launch {
                        db.assessmentDao().upsertAll(items)
                    }
                }
            }
    }

    fun startSubjectsListener(userId: String): ListenerRegistration {
        return firestore.collection("users").document(userId).collection("subjects")
            .addSnapshotListener { snap, _ ->
                if (snap != null) {
                    val now = System.currentTimeMillis()
                    val items = snap.documents.mapNotNull { d ->
                        val id = d.id
                        val name = d.getString("name") ?: return@mapNotNull null
                        val icon = d.getString("icon") ?: "\uD83D\uDCD6"
                        SubjectEntity(id = id, name = name, icon = icon, updatedAt = now, userId = userId, pendingOp = null)
                    }
                    CoroutineScope(Dispatchers.IO).launch {
                        if (items.isNotEmpty()) items.forEach { db.subjectDao().upsert(it) }
                    }
                }
            }
    }
}


