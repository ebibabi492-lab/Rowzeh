package com.example.service

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.example.data.local.AppDatabase
import com.example.data.repository.RowzehRepository
import com.example.widget.RowzehAppWidgetProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RowzehAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        Log.d("RowzehAlarmReceiver", "Received broadcast: $action")

        val db = AppDatabase.getInstance(context)
        val repository = RowzehRepository(db.trackDao(), db.scheduleDao(), db.timeIntervalDao())

        if (action == Intent.ACTION_BOOT_COMPLETED) {
            // Re-schedule alarm on boot
            CoroutineScope(Dispatchers.IO).launch {
                val schedule = repository.getScheduleSync()
                val intervals = repository.getEnabledIntervalsSync()
                if (schedule != null && schedule.isEnabled) {
                    val nextTime = RowzehScheduler.scheduleNextAlarm(context, schedule, intervals)
                    repository.updateNextSchedule(nextTime)
                }
            }
            return
        }

        if (action == RowzehScheduler.ACTION_ROWZEH_ALARM) {
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val schedule = repository.getScheduleSync()
                    val intervals = repository.getEnabledIntervalsSync()
                    if (schedule != null && schedule.isEnabled) {
                        // 1. Pick a random track from user's selected list
                        val track = repository.getRandomIncludedTrack()

                        // 2. Post the pre-alert notification: "زمان روضه"
                        val alertTitle = track?.title ?: "روضه و مرثیه شریفه"
                        val notification = RowzehNotificationHelper.buildAlertNotification(
                            context,
                            alertTitle
                        )
                        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                        nm.notify(RowzehNotificationHelper.NOTIFICATION_ALERT_ID, notification)

                        // 3. Play the audio file via Foreground Service
                        if (track != null) {
                            RowzehPlaybackService.startPlayTrack(
                                context = context,
                                filePath = track.filePath,
                                title = track.title,
                                speaker = track.speaker,
                                volumePercent = schedule.volumePercent
                            )
                        }

                        // 4. Schedule the next alarm within the repetition rules
                        val nextMillis = RowzehScheduler.scheduleNextAlarm(context, schedule, intervals)
                        repository.updateNextSchedule(nextMillis)

                        // 5. Update Home Screen Widget
                        RowzehAppWidgetProvider.updateAllWidgets(context)
                    }
                } catch (e: Exception) {
                    Log.e("RowzehAlarmReceiver", "Error handling rowzeh alarm", e)
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
