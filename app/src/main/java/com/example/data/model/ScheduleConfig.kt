package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "schedule_config")
data class ScheduleConfig(
    @PrimaryKey
    val id: Int = 1,
    val isEnabled: Boolean = true,
    val startHour: Int = 18,
    val startMinute: Int = 0,
    val endHour: Int = 21,
    val endMinute: Int = 0,
    val repeatMode: String = "DAILY", // "DAILY" or "WEEKLY"
    val weeklyDaysMask: Int = 127, // 7 days (all 7 bits set)
    val volumePercent: Int = 85, // 0 to 100
    val lastPlayedTitle: String? = null,
    val lastPlayedTimestamp: Long = 0L,
    val nextScheduledTimestamp: Long = 0L
)
