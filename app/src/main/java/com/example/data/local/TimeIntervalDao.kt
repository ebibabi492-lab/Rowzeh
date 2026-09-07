package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.TimeInterval
import kotlinx.coroutines.flow.Flow

@Dao
interface TimeIntervalDao {

    @Query("SELECT * FROM time_intervals ORDER BY startHour ASC, startMinute ASC")
    fun getAllIntervals(): Flow<List<TimeInterval>>

    @Query("SELECT * FROM time_intervals WHERE isEnabled = 1 ORDER BY startHour ASC, startMinute ASC")
    suspend fun getEnabledIntervals(): List<TimeInterval>

    @Query("SELECT * FROM time_intervals ORDER BY startHour ASC, startMinute ASC")
    suspend fun getAllIntervalsSync(): List<TimeInterval>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInterval(interval: TimeInterval): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIntervals(intervals: List<TimeInterval>)

    @Update
    suspend fun updateInterval(interval: TimeInterval)

    @Delete
    suspend fun deleteInterval(interval: TimeInterval)

    @Query("UPDATE time_intervals SET isEnabled = :enabled WHERE id = :id")
    suspend fun setEnabled(id: Long, enabled: Boolean)

    @Query("SELECT COUNT(*) FROM time_intervals")
    suspend fun getCount(): Int
}
