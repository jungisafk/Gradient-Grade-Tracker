package com.example.gradientgradetracker.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DbMigrations {
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Add nullable columns so existing rows are preserved
            database.execSQL("ALTER TABLE subjects ADD COLUMN userId TEXT")
            database.execSQL("ALTER TABLE subjects ADD COLUMN pendingOp TEXT")
        }
    }
}


