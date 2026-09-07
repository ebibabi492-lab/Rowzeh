package com.example.service

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.util.Log
import com.example.data.local.AppDatabase
import com.example.data.repository.RowzehRepository
import com.example.widget.RowzehAppWidgetProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class RowzehPlaybackService : Service() {

    companion object {
        const val ACTION_PLAY_TRACK = "com.example.ACTION_PLAY_TRACK"
        const val ACTION_PLAY_RANDOM = "com.example.ACTION_PLAY_RANDOM"
        const val ACTION_STOP = "com.example.ACTION_STOP"
        const val ACTION_PAUSE = "com.example.ACTION_PAUSE"
        const val ACTION_RESUME = "com.example.ACTION_RESUME"

        const val EXTRA_FILE_PATH = "extra_file_path"
        const val EXTRA_TRACK_TITLE = "extra_track_title"
        const val EXTRA_SPEAKER = "extra_speaker"
        const val EXTRA_VOLUME = "extra_volume"

        private val _currentPlayingTrack = MutableStateFlow<PlayingState?>(null)
        val currentPlayingTrack = _currentPlayingTrack.asStateFlow()

        data class PlayingState(
            val title: String,
            val speaker: String,
            val filePath: String,
            val isPlaying: Boolean,
            val currentPosMs: Int,
            val totalDurationMs: Int
        )

        fun startPlayTrack(
            context: Context,
            filePath: String,
            title: String,
            speaker: String,
            volumePercent: Int = 85
        ) {
            val intent = Intent(context, RowzehPlaybackService::class.java).apply {
                action = ACTION_PLAY_TRACK
                putExtra(EXTRA_FILE_PATH, filePath)
                putExtra(EXTRA_TRACK_TITLE, title)
                putExtra(EXTRA_SPEAKER, speaker)
                putExtra(EXTRA_VOLUME, volumePercent)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stopPlayback(context: Context) {
            val intent = Intent(context, RowzehPlaybackService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }
    }

    private var mediaPlayer: MediaPlayer? = null
    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)
    private lateinit var repository: RowzehRepository

    private var activeTitle: String = ""
    private var activeSpeaker: String = ""
    private var activeFilePath: String = ""
    private var currentVolumePercent: Int = 85

    override fun onCreate() {
        super.onCreate()
        val db = AppDatabase.getInstance(this)
        repository = RowzehRepository(db.trackDao(), db.scheduleDao())
        RowzehNotificationHelper.createNotificationChannels(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY_TRACK -> {
                val filePath = intent.getStringExtra(EXTRA_FILE_PATH) ?: ""
                val title = intent.getStringExtra(EXTRA_TRACK_TITLE) ?: "روضه شریفه"
                val speaker = intent.getStringExtra(EXTRA_SPEAKER) ?: "آوای معنوی"
                val volume = intent.getIntExtra(EXTRA_VOLUME, 85)
                playAudioFile(filePath, title, speaker, volume)
            }
            ACTION_PLAY_RANDOM -> {
                serviceScope.launch {
                    val track = repository.getRandomIncludedTrack()
                    val schedule = repository.getScheduleSync()
                    val vol = schedule?.volumePercent ?: 85
                    if (track != null) {
                        playAudioFile(track.filePath, track.title, track.speaker, vol)
                    } else {
                        stopSelf()
                    }
                }
            }
            ACTION_PAUSE -> {
                mediaPlayer?.let {
                    if (it.isPlaying) {
                        it.pause()
                        updatePlayingState(false)
                    }
                }
            }
            ACTION_RESUME -> {
                mediaPlayer?.let {
                    if (!it.isPlaying) {
                        it.start()
                        updatePlayingState(true)
                    }
                }
            }
            ACTION_STOP -> {
                stopPlaying()
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }

    private fun playAudioFile(
        filePath: String,
        title: String,
        speaker: String,
        volumePercent: Int
    ) {
        stopPlaying()

        activeTitle = title
        activeSpeaker = speaker
        activeFilePath = filePath
        currentVolumePercent = volumePercent

        val file = File(filePath)
        if (!file.exists()) {
            Log.e("RowzehPlaybackService", "File not found: $filePath")
            stopSelf()
            return
        }

        // Show foreground notification
        val notification = RowzehNotificationHelper.buildPlaybackNotification(
            this,
            title,
            speaker,
            isPlaying = true
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                RowzehNotificationHelper.NOTIFICATION_PLAYBACK_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            )
        } else {
            startForeground(RowzehNotificationHelper.NOTIFICATION_PLAYBACK_ID, notification)
        }

        try {
            val player = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(filePath)

                // Apply volume setting proportionally
                val volFloat = (volumePercent.coerceIn(5, 100) / 100f)
                setVolume(volFloat, volFloat)

                prepare()
                start()
            }

            mediaPlayer = player

            updatePlayingState(true)
            RowzehAppWidgetProvider.updateAllWidgets(this)

            // Track last played
            serviceScope.launch {
                repository.updateLastPlayed(title, System.currentTimeMillis())
            }

            player.setOnCompletionListener {
                Log.d("RowzehPlaybackService", "Track completed: $title")
                stopPlaying()
                stopSelf()
            }

            player.setOnErrorListener { _, what, extra ->
                Log.e("RowzehPlaybackService", "MediaPlayer error: $what, $extra")
                stopPlaying()
                stopSelf()
                true
            }

        } catch (e: Exception) {
            Log.e("RowzehPlaybackService", "Failed to start media player", e)
            stopPlaying()
            stopSelf()
        }
    }

    private fun updatePlayingState(isPlaying: Boolean) {
        val duration = mediaPlayer?.duration ?: 0
        val pos = mediaPlayer?.currentPosition ?: 0
        _currentPlayingTrack.value = PlayingState(
            title = activeTitle,
            speaker = activeSpeaker,
            filePath = activeFilePath,
            isPlaying = isPlaying,
            currentPosMs = pos,
            totalDurationMs = duration
        )
    }

    private fun stopPlaying() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.release()
            }
        } catch (e: Exception) {
            Log.w("RowzehPlaybackService", "Error releasing player", e)
        } finally {
            mediaPlayer = null
            _currentPlayingTrack.value = null
            RowzehAppWidgetProvider.updateAllWidgets(this)
        }
    }

    override fun onDestroy() {
        stopPlaying()
        serviceJob.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
