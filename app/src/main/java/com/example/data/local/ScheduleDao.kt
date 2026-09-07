package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ScheduleConfig
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleDao {
    @Query("SELECT * FROM schedule_config WHERE id = 1 LIMIT 1")
    fun getScheduleFlow(): Flow<ScheduleConfig?>

    @Query("SELECT * FROM schedule_config WHERE id = 1 LIMIT 1")
    suspend fun getScheduleSync(): ScheduleConfig?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSchedule(schedule: ScheduleConfig)

    @Update
    suspend fun updateSchedule(schedule: ScheduleConfig)

    @Query("UPDATE schedule_config SET isEnabled = :enabled WHERE id = 1")
    suspend fun setEnabled(enabled: Boolean)

    @Query("UPDATE schedule_config SET volumePercent = :volume WHERE id = 1")
    suspend fun setVolume(volume: Int)

    @Query("UPDATE schedule_config SET lastPlayedTitle = :title, lastPlayedTimestamp = :timestamp WHERE id = 1")
    suspend fun updateLastPlayed(title: String, timestamp: Long)

    @Query("UPDATE schedule_config SET nextScheduledTimestamp = :timestamp WHERE id = 1")
    suspend fun updateNextSchedule(timestamp: Long)
}
