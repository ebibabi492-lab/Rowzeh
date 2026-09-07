package com.example.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.RowzehApplication
import com.example.audio.AudioFileImporter
import com.example.audio.AudioRecorderHelper
import com.example.data.model.RowzehTrack
import com.example.data.model.ScheduleConfig
import com.example.data.model.TimeInterval
import com.example.service.RowzehPlaybackService
import com.example.service.RowzehScheduler
import com.example.widget.RowzehAppWidgetProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RowzehViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as RowzehApplication).repository
    private val recorderHelper = AudioRecorderHelper(application)

    val allTracks: StateFlow<List<RowzehTrack>> = repository.allTracks
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val scheduleConfig: StateFlow<ScheduleConfig?> = repository.scheduleFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val allIntervals: StateFlow<List<TimeInterval>> = repository.allIntervals
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val playingState = RowzehPlaybackService.currentPlayingTrack

    // Recording State
    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _recordedDurationSec = MutableStateFlow(0)
    val recordedDurationSec: StateFlow<Int> = _recordedDurationSec.asStateFlow()

    private var recordTimerJob: kotlinx.coroutines.Job? = null

    // Pending Record Result waiting for user confirmation/naming
    private val _pendingRecordedResult = MutableStateFlow<AudioRecorderHelper.RecordResult?>(null)
    val pendingRecordedResult: StateFlow<AudioRecorderHelper.RecordResult?> = _pendingRecordedResult.asStateFlow()

    // Alert Dialog State for "زمان روضه" pre-alert
    private val _showRowzehAlert = MutableStateFlow<RowzehAlertData?>(null)
    val showRowzehAlert: StateFlow<RowzehAlertData?> = _showRowzehAlert.asStateFlow()

    data class RowzehAlertData(
        val track: RowzehTrack,
        val countdownSeconds: Int = 3
    )

    private suspend fun rescheduleIfEnabled() {
        val current = repository.getScheduleSync()
        if (current != null) {
            if (current.isEnabled) {
                val intervals = repository.getEnabledIntervalsSync()
                val nextTime = RowzehScheduler.scheduleNextAlarm(getApplication(), current, intervals)
                repository.updateNextSchedule(nextTime)
            } else {
                RowzehScheduler.cancelAlarm(getApplication())
            }
        }
        RowzehAppWidgetProvider.updateAllWidgets(getApplication())
    }

    fun toggleScheduleEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.toggleScheduleEnabled(enabled)
            val current = repository.getScheduleSync()
            if (current != null) {
                if (enabled) {
                    val intervals = repository.getEnabledIntervalsSync()
                    val nextTime = RowzehScheduler.scheduleNextAlarm(getApplication(), current.copy(isEnabled = true), intervals)
                    repository.updateNextSchedule(nextTime)
                } else {
                    RowzehScheduler.cancelAlarm(getApplication())
                }
            }
            RowzehAppWidgetProvider.updateAllWidgets(getApplication())
        }
    }

    fun updateTimeWindow(startH: Int, startM: Int, endH: Int, endM: Int) {
        viewModelScope.launch {
            val current = repository.getScheduleSync() ?: ScheduleConfig()
            val updated = current.copy(
                startHour = startH,
                startMinute = startM,
                endHour = endH,
                endMinute = endM
            )
            repository.saveSchedule(updated)
            rescheduleIfEnabled()
        }
    }

    fun updateRepeatMode(mode: String) {
        viewModelScope.launch {
            val current = repository.getScheduleSync() ?: ScheduleConfig()
            val updated = current.copy(repeatMode = mode)
            repository.saveSchedule(updated)
            rescheduleIfEnabled()
        }
    }

    fun toggleWeeklyDay(dayIndex: Int) {
        viewModelScope.launch {
            val current = repository.getScheduleSync() ?: ScheduleConfig()
            val mask = current.weeklyDaysMask xor (1 shl dayIndex)
            val updated = current.copy(weeklyDaysMask = mask)
            repository.saveSchedule(updated)
            rescheduleIfEnabled()
        }
    }

    fun addInterval(title: String, startHour: Int, startMinute: Int, endHour: Int, endMinute: Int) {
        viewModelScope.launch {
            repository.insertInterval(
                TimeInterval(
                    title = title,
                    startHour = startHour,
                    startMinute = startMinute,
                    endHour = endHour,
                    endMinute = endMinute,
                    isEnabled = true
                )
            )
            rescheduleIfEnabled()
        }
    }

    fun updateInterval(interval: TimeInterval) {
        viewModelScope.launch {
            repository.updateInterval(interval)
            rescheduleIfEnabled()
        }
    }

    fun deleteInterval(interval: TimeInterval) {
        viewModelScope.launch {
            repository.deleteInterval(interval)
            rescheduleIfEnabled()
        }
    }

    fun toggleIntervalEnabled(interval: TimeInterval) {
        viewModelScope.launch {
            repository.toggleIntervalEnabled(interval.id, !interval.isEnabled)
            rescheduleIfEnabled()
        }
    }

    fun updateVolume(volumePercent: Int) {
        viewModelScope.launch {
            repository.setVolume(volumePercent)
            // If playing, update volume live
            val current = repository.getScheduleSync()
            if (current != null) {
                // Live volume update could be applied
            }
        }
    }

    fun toggleTrackInclusion(track: RowzehTrack) {
        viewModelScope.launch {
            repository.setTrackIncluded(track.id, !track.isIncludedInRandom)
        }
    }

    fun deleteTrack(track: RowzehTrack) {
        viewModelScope.launch {
            if (playingState.value?.filePath == track.filePath) {
                stopPlayback()
            }
            repository.deleteTrack(track)
        }
    }

    fun playTrack(track: RowzehTrack) {
        viewModelScope.launch {
            val current = repository.getScheduleSync()
            val vol = current?.volumePercent ?: 85
            RowzehPlaybackService.startPlayTrack(
                context = getApplication(),
                filePath = track.filePath,
                title = track.title,
                speaker = track.speaker,
                volumePercent = vol
            )
        }
    }

    fun stopPlayback() {
        RowzehPlaybackService.stopPlayback(getApplication())
    }

    // Trigger instant test of random playback with "زمان روضه" pre-alert
    fun triggerInstantRandomTest() {
        viewModelScope.launch {
            val track = repository.getRandomIncludedTrack()
            if (track != null) {
                _showRowzehAlert.value = RowzehAlertData(track = track, countdownSeconds = 3)
            }
        }
    }

    fun dismissRowzehAlert() {
        _showRowzehAlert.value = null
    }

    fun confirmPlayFromAlert() {
        val alertData = _showRowzehAlert.value
        _showRowzehAlert.value = null
        if (alertData != null) {
            playTrack(alertData.track)
        }
    }

    // Audio Recording
    fun startVoiceRecording(): Boolean {
        val file = recorderHelper.startRecording()
        if (file != null) {
            _isRecording.value = true
            _recordedDurationSec.value = 0
            recordTimerJob?.cancel()
            recordTimerJob = viewModelScope.launch {
                while (_isRecording.value) {
                    kotlinx.coroutines.delay(1000)
                    _recordedDurationSec.value += 1
                }
            }
            return true
        }
        return false
    }

    fun stopVoiceRecording() {
        recordTimerJob?.cancel()
        val result = recorderHelper.stopRecording()
        _isRecording.value = false
        if (result != null) {
            _pendingRecordedResult.value = result
        }
    }

    fun cancelVoiceRecording() {
        recordTimerJob?.cancel()
        recorderHelper.cancelRecording()
        _isRecording.value = false
        _pendingRecordedResult.value = null
    }

    fun saveRecordedTrack(title: String, speaker: String) {
        val pending = _pendingRecordedResult.value ?: return
        viewModelScope.launch {
            val track = RowzehTrack(
                title = title.ifBlank { "روضه ضبط‌شده" },
                speaker = speaker.ifBlank { "صدای ضبط‌شده کاربر" },
                filePath = pending.filePath,
                isBuiltIn = false,
                isRecorded = true,
                isIncludedInRandom = true,
                durationSeconds = pending.durationSeconds
            )
            repository.insertTrack(track)
            _pendingRecordedResult.value = null
        }
    }

    fun dismissPendingRecording() {
        _pendingRecordedResult.value = null
    }

    // Audio File Import
    fun importAudioFile(uri: Uri) {
        viewModelScope.launch {
            val track = AudioFileImporter.importAudioFromUri(getApplication(), uri)
            if (track != null) {
                repository.insertTrack(track)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        recorderHelper.cancelRecording()
    }
}

class RowzehViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RowzehViewModel::class.java)) {
            return RowzehViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
