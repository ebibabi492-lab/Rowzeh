package com.example.data.repository

import android.content.Context
import com.example.data.local.ScheduleDao
import com.example.data.local.TimeIntervalDao
import com.example.data.local.TrackDao
import com.example.data.model.RowzehTrack
import com.example.data.model.ScheduleConfig
import com.example.data.model.TimeInterval
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File

class RowzehRepository(
    private val trackDao: TrackDao,
    private val scheduleDao: ScheduleDao,
    private val timeIntervalDao: TimeIntervalDao
) {

    val allTracks: Flow<List<RowzehTrack>> = trackDao.getAllTracks()
    val includedTracks: Flow<List<RowzehTrack>> = trackDao.getIncludedTracks()
    val scheduleFlow: Flow<ScheduleConfig?> = scheduleDao.getScheduleFlow()
    val allIntervals: Flow<List<TimeInterval>> = timeIntervalDao.getAllIntervals()

    suspend fun initializeDefaultsIfEmpty(context: Context) = withContext(Dispatchers.IO) {
        // Permanently purge any default/built-in sample files and tracks as requested
        try {
            trackDao.deleteBuiltInTracks()
            val samplesDir = File(context.filesDir, "samples")
            if (samplesDir.exists()) {
                samplesDir.deleteRecursively()
            }
        } catch (_: Exception) {}

        val schedule = scheduleDao.getScheduleSync()
        if (schedule == null) {
            scheduleDao.saveSchedule(
                ScheduleConfig(
                    id = 1,
                    isEnabled = true,
                    startHour = 18,
                    startMinute = 0,
                    endHour = 21,
                    endMinute = 0,
                    repeatMode = "DAILY",
                    weeklyDaysMask = 127, // All 7 days (Saturday to Friday)
                    volumePercent = 85
                )
            )
        }

        val intervalCount = timeIntervalDao.getCount()
        if (intervalCount == 0) {
            timeIntervalDao.insertIntervals(
                listOf(
                    TimeInterval(
                        title = "بازه صبح و پیش‌ازظهر",
                        startHour = 8,
                        startMinute = 0,
                        endHour = 11,
                        endMinute = 0,
                        isEnabled = false
                    ),
                    TimeInterval(
                        title = "بازه بعدازظهر",
                        startHour = 13,
                        startMinute = 0,
                        endHour = 16,
                        endMinute = 0,
                        isEnabled = false
                    ),
                    TimeInterval(
                        title = "بازه غروب تا شب",
                        startHour = 18,
                        startMinute = 0,
                        endHour = 21,
                        endMinute = 0,
                        isEnabled = true
                    )
                )
            )
        }
    }

    suspend fun insertTrack(track: RowzehTrack): Long = withContext(Dispatchers.IO) {
        trackDao.insertTrack(track)
    }

    suspend fun deleteTrack(track: RowzehTrack) = withContext(Dispatchers.IO) {
        trackDao.deleteTrack(track)
    }

    suspend fun setTrackIncluded(id: Long, included: Boolean) = withContext(Dispatchers.IO) {
        trackDao.setTrackIncluded(id, included)
    }

    suspend fun getScheduleSync(): ScheduleConfig? = withContext(Dispatchers.IO) {
        scheduleDao.getScheduleSync()
    }

    suspend fun saveSchedule(schedule: ScheduleConfig) = withContext(Dispatchers.IO) {
        scheduleDao.saveSchedule(schedule)
    }

    suspend fun toggleScheduleEnabled(enabled: Boolean) = withContext(Dispatchers.IO) {
        scheduleDao.setEnabled(enabled)
    }

    suspend fun setVolume(volume: Int) = withContext(Dispatchers.IO) {
        scheduleDao.setVolume(volume.coerceIn(0, 100))
    }

    suspend fun getRandomIncludedTrack(): RowzehTrack? = withContext(Dispatchers.IO) {
        val tracks = trackDao.getIncludedTracksSync()
        if (tracks.isNotEmpty()) {
            tracks.random()
        } else {
            val allUser = trackDao.getAllUserTracksSync()
            if (allUser.isNotEmpty()) {
                allUser.random()
            } else {
                null
            }
        }
    }

    suspend fun updateLastPlayed(title: String, timestamp: Long) = withContext(Dispatchers.IO) {
        scheduleDao.updateLastPlayed(title, timestamp)
    }

    suspend fun updateNextSchedule(timestamp: Long) = withContext(Dispatchers.IO) {
        scheduleDao.updateNextSchedule(timestamp)
    }

    suspend fun getEnabledIntervalsSync(): List<TimeInterval> = withContext(Dispatchers.IO) {
        timeIntervalDao.getEnabledIntervals()
    }

    suspend fun getAllIntervalsSync(): List<TimeInterval> = withContext(Dispatchers.IO) {
        timeIntervalDao.getAllIntervalsSync()
    }

    suspend fun insertInterval(interval: TimeInterval): Long = withContext(Dispatchers.IO) {
        timeIntervalDao.insertInterval(interval)
    }

    suspend fun updateInterval(interval: TimeInterval) = withContext(Dispatchers.IO) {
        timeIntervalDao.updateInterval(interval)
    }

    suspend fun deleteInterval(interval: TimeInterval) = withContext(Dispatchers.IO) {
        timeIntervalDao.deleteInterval(interval)
    }

    suspend fun toggleIntervalEnabled(id: Long, enabled: Boolean) = withContext(Dispatchers.IO) {
        timeIntervalDao.setEnabled(id, enabled)
    }
}
