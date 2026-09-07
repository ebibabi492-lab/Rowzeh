package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rowzeh_tracks")
data class RowzehTrack(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val speaker: String,
    val filePath: String,
    val isBuiltIn: Boolean = false,
    val isRecorded: Boolean = false,
    val isIncludedInRandom: Boolean = true,
    val durationSeconds: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
