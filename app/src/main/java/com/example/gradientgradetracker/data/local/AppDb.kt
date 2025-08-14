package com.example.gradientgradetracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "subjects")
data class SubjectEntity(
    @PrimaryKey val id: String,
    val name: String,
    val icon: String,
    val updatedAt: Long,
    val userId: String?,
    val pendingOp: String?
)

@Entity(tableName = "assessments")
data class AssessmentEntity(
    @PrimaryKey val localId: String,
    val remoteId: String?,
    val subjectId: String,
    val period: String,
    val type: String,
    val score: Double,
    val total: Double,
    val weight: Double,
    val date: String,
    val updatedAt: Long,
    val pendingOp: String?
)

@Dao
interface SubjectDao {
    @Query("SELECT * FROM subjects ORDER BY name")
    fun observeSubjects(): Flow<List<SubjectEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(subject: SubjectEntity)

    @Query("SELECT * FROM subjects WHERE pendingOp IS NOT NULL")
    suspend fun getPending(): List<SubjectEntity>

    @Query("UPDATE subjects SET pendingOp = NULL, updatedAt = :now WHERE id = :id")
    suspend fun markSynced(id: String, now: Long)

    @Query("SELECT * FROM subjects WHERE userId IS NULL OR pendingOp IS NOT NULL")
    suspend fun getNeedingRemoteSync(): List<SubjectEntity>
}

@Dao
interface AssessmentDao {
    @Query("SELECT * FROM assessments WHERE subjectId = :subjectId ORDER BY updatedAt DESC")
    fun observeAssessments(subjectId: String): Flow<List<AssessmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<AssessmentEntity>)

    @Query("SELECT * FROM assessments WHERE pendingOp IS NOT NULL")
    suspend fun getPending(): List<AssessmentEntity>

    @Query("UPDATE assessments SET pendingOp = NULL, remoteId = :remoteId, updatedAt = :now WHERE localId = :localId")
    suspend fun markSynced(localId: String, remoteId: String?, now: Long)
}

@Database(entities = [SubjectEntity::class, AssessmentEntity::class], version = 2)
abstract class AppDb : RoomDatabase() {
    abstract fun subjectDao(): SubjectDao
    abstract fun assessmentDao(): AssessmentDao
}


