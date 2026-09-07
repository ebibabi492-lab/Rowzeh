package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R
import com.example.data.local.AppDatabase
import com.example.data.repository.RowzehRepository
import com.example.service.RowzehPlaybackService
import com.example.service.RowzehScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

class RowzehAppWidgetProvider : AppWidgetProvider() {

    companion object {
        const val ACTION_TOGGLE_SCHEDULE = "com.example.ACTION_TOGGLE_SCHEDULE"
        const val ACTION_PLAY_RANDOM = "com.example.ACTION_PLAY_RANDOM"
        const val ACTION_STOP_PLAYBACK = "com.example.ACTION_STOP_PLAYBACK"

        fun updateAllWidgets(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName = ComponentName(context, RowzehAppWidgetProvider::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
            if (appWidgetIds.isNotEmpty()) {
                val intent = Intent(context, RowzehAppWidgetProvider::class.java).apply {
                    action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
                }
                context.sendBroadcast(intent)
            }
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val db = AppDatabase.getInstance(context)
        val repository = RowzehRepository(db.trackDao(), db.scheduleDao())

        CoroutineScope(Dispatchers.IO).launch {
            val schedule = repository.getScheduleSync()
            val playingState = RowzehPlaybackService.currentPlayingTrack.value

            for (appWidgetId in appWidgetIds) {
                val views = RemoteViews(context.packageName, R.layout.widget_rowzeh_layout)

                // 1. Root click opens MainActivity
                val mainIntent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
                val pendingMain = PendingIntent.getActivity(
                    context,
                    100,
                    mainIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                views.setOnClickPendingIntent(R.id.widget_root, pendingMain)

                // 2. Status texts
                val isPlaying = playingState?.isPlaying == true
                val isEnabled = schedule?.isEnabled == true

                if (isPlaying) {
                    views.setTextViewText(R.id.widget_tv_substatus, "در حال پخش روضه...")
                    views.setTextViewText(R.id.widget_badge_state, "در حال پخش")
                    views.setTextViewText(
                        R.id.widget_tv_detail,
                        "«${playingState?.title ?: "روضه شریفه"}»"
                    )

                    // Action button: Stop
                    views.setTextViewText(R.id.widget_btn_action, "توقف روضه")
                    val stopIntent = Intent(context, RowzehAppWidgetProvider::class.java).apply {
                        action = ACTION_STOP_PLAYBACK
                    }
                    val pendingStop = PendingIntent.getBroadcast(
                        context,
                        101,
                        stopIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    views.setOnClickPendingIntent(R.id.widget_btn_action, pendingStop)

                } else {
                    if (isEnabled) {
                        val startStr = String.format(
                            Locale.getDefault(),
                            "%02d:%02d",
                            schedule?.startHour ?: 18,
                            schedule?.startMinute ?: 0
                        )
                        val endStr = String.format(
                            Locale.getDefault(),
                            "%02d:%02d",
                            schedule?.endHour ?: 21,
                            schedule?.endMinute ?: 0
                        )
                        val modeStr = if (schedule?.repeatMode == "WEEKLY") "هفتگی" else "روزانه"

                        views.setTextViewText(R.id.widget_tv_substatus, "برنامه زمانبندی فعال است")
                        views.setTextViewText(R.id.widget_badge_state, "فعال")
                        views.setTextViewText(
                            R.id.widget_tv_detail,
                            "بازه پخش تصادفی: $startStr تا $endStr ($modeStr)"
                        )
                    } else {
                        views.setTextViewText(R.id.widget_tv_substatus, "برنامه زمانبندی غیرفعال است")
                        views.setTextViewText(R.id.widget_badge_state, "خاموش")
                        views.setTextViewText(
                            R.id.widget_tv_detail,
                            "برای پخش خودکار، برنامه را فعال کنید"
                        )
                    }

                    // Action button: Play Random Now
                    views.setTextViewText(R.id.widget_btn_action, "پخش فوری روضه")
                    val playIntent = Intent(context, RowzehAppWidgetProvider::class.java).apply {
                        action = ACTION_PLAY_RANDOM
                    }
                    val pendingPlay = PendingIntent.getBroadcast(
                        context,
                        102,
                        playIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    views.setOnClickPendingIntent(R.id.widget_btn_action, pendingPlay)
                }

                // 3. Toggle button
                val toggleLabel = if (isEnabled) "خاموش کردن" else "روشن کردن"
                views.setTextViewText(R.id.widget_btn_toggle, toggleLabel)

                val toggleIntent = Intent(context, RowzehAppWidgetProvider::class.java).apply {
                    action = ACTION_TOGGLE_SCHEDULE
                }
                val pendingToggle = PendingIntent.getBroadcast(
                    context,
                    103,
                    toggleIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                views.setOnClickPendingIntent(R.id.widget_btn_toggle, pendingToggle)

                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        val action = intent.action
        val db = AppDatabase.getInstance(context)
        val repository = RowzehRepository(db.trackDao(), db.scheduleDao())

        when (action) {
            ACTION_TOGGLE_SCHEDULE -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val schedule = repository.getScheduleSync()
                    if (schedule != null) {
                        val newEnabled = !schedule.isEnabled
                        repository.toggleScheduleEnabled(newEnabled)
                        if (newEnabled) {
                            val nextTime = RowzehScheduler.scheduleNextAlarm(context, schedule.copy(isEnabled = true))
                            repository.updateNextSchedule(nextTime)
                        } else {
                            RowzehScheduler.cancelAlarm(context)
                        }
                        updateAllWidgets(context)
                    }
                }
            }
            ACTION_PLAY_RANDOM -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val track = repository.getRandomIncludedTrack()
                    val schedule = repository.getScheduleSync()
                    val vol = schedule?.volumePercent ?: 85
                    if (track != null) {
                        RowzehPlaybackService.startPlayTrack(
                            context = context,
                            filePath = track.filePath,
                            title = track.title,
                            speaker = track.speaker,
                            volumePercent = vol
                        )
                    }
                    updateAllWidgets(context)
                }
            }
            ACTION_STOP_PLAYBACK -> {
                RowzehPlaybackService.stopPlayback(context)
                updateAllWidgets(context)
            }
        }
    }
}
