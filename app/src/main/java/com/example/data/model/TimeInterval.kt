package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "time_intervals")
data class TimeInterval(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val startHour: Int,
    val startMinute: Int,
    val endHour: Int,
    val endMinute: Int,
    val isEnabled: Boolean = true
)
