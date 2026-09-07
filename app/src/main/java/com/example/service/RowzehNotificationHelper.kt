package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R

object RowzehNotificationHelper {

    const val CHANNEL_ALERT_ID = "rowzeh_alert_channel"
    const val CHANNEL_PLAYBACK_ID = "rowzeh_playback_channel"
    const val NOTIFICATION_ALERT_ID = 2001
    const val NOTIFICATION_PLAYBACK_ID = 2002

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Alert Channel (High Importance for "زمان روضه" warning)
            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()

            val alertChannel = NotificationChannel(
                CHANNEL_ALERT_ID,
                context.getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.notification_channel_desc)
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 350, 200, 350)
                setSound(defaultSoundUri, audioAttributes)
            }

            // Playback Channel
            val playbackChannel = NotificationChannel(
                CHANNEL_PLAYBACK_ID,
                "پخش روضه",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "نمایش کنترل‌های پخش روضه"
            }

            notificationManager.createNotificationChannel(alertChannel)
            notificationManager.createNotificationChannel(playbackChannel)
        }
    }

    fun buildAlertNotification(context: Context, trackTitle: String): Notification {
        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingOpen = PendingIntent.getActivity(
            context,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(context, CHANNEL_ALERT_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("زمان روضه")
            .setContentText("پخش صوتی: $trackTitle")
            .setSubText("ساعت روضه")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setContentIntent(pendingOpen)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 400, 200, 400))
            .build()
    }

    fun buildPlaybackNotification(
        context: Context,
        trackTitle: String,
        speaker: String,
        isPlaying: Boolean
    ): Notification {
        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingOpen = PendingIntent.getActivity(
            context,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = Intent(context, RowzehPlaybackService::class.java).apply {
            action = RowzehPlaybackService.ACTION_STOP
        }
        val pendingStop = PendingIntent.getService(
            context,
            1,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(context, CHANNEL_PLAYBACK_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("در حال پخش: $trackTitle")
            .setContentText(speaker)
            .setSubText("زمان روضه")
            .setOngoing(isPlaying)
            .setContentIntent(pendingOpen)
            .addAction(
                android.R.drawable.ic_media_pause,
                "توقف",
                pendingStop
            )
            .build()
    }
}
