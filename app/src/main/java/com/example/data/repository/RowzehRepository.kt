package com.example.data.repository

import android.content.Context
import com.example.audio.AudioSampleGenerator
import com.example.data.local.ScheduleDao
import com.example.data.local.TrackDao
import com.example.data.model.RowzehTrack
import com.example.data.model.ScheduleConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class RowzehRepository(
    private val trackDao: TrackDao,
    private val scheduleDao: ScheduleDao
) {
    val allTracks: Flow<List<RowzehTrack>> = trackDao.getAllTracks()
    val includedTracks: Flow<List<RowzehTrack>> = trackDao.getIncludedTracks()
    val scheduleFlow: Flow<ScheduleConfig?> = scheduleDao.getScheduleFlow()

    suspend fun initializeDefaultsIfEmpty(context: Context) = withContext(Dispatchers.IO) {
        val count = trackDao.getTrackCount()
        if (count == 0) {
            val samples = AudioSampleGenerator.generateDefaultSamplesIfEmpty(context)
            trackDao.insertTracks(samples)
        }

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
            // If none are checked, fallback to any available track
            val any = trackDao.getTrackById(1)
            any
        }
    }

    suspend fun updateLastPlayed(title: String, timestamp: Long) = withContext(Dispatchers.IO) {
        scheduleDao.updateLastPlayed(title, timestamp)
    }

    suspend fun updateNextSchedule(timestamp: Long) = withContext(Dispatchers.IO) {
        scheduleDao.updateNextSchedule(timestamp)
    }
}
