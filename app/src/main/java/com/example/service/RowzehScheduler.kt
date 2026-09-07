package com.example.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.data.model.ScheduleConfig
import java.util.Calendar
import kotlin.random.Random

object RowzehScheduler {

    const val ACTION_ROWZEH_ALARM = "com.example.ACTION_ROWZEH_ALARM"
    private const val ALARM_REQUEST_CODE = 1001

    fun scheduleNextAlarm(context: Context, schedule: ScheduleConfig): Long {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, RowzehAlarmReceiver::class.java).apply {
            action = ACTION_ROWZEH_ALARM
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (!schedule.isEnabled) {
            alarmManager.cancel(pendingIntent)
            Log.d("RowzehScheduler", "Schedule is disabled, alarm cancelled")
            return 0L
        }

        val nextTriggerMillis = calculateNextTriggerTime(schedule)

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        nextTriggerMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        nextTriggerMillis,
                        pendingIntent
                    )
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    nextTriggerMillis,
                    pendingIntent
                )
            }
            Log.d("RowzehScheduler", "Scheduled alarm for: $nextTriggerMillis")
        } catch (e: Exception) {
            Log.e("RowzehScheduler", "Error scheduling alarm", e)
        }

        return nextTriggerMillis
    }

    fun cancelAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, RowzehAlarmReceiver::class.java).apply {
            action = ACTION_ROWZEH_ALARM
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    fun calculateNextTriggerTime(schedule: ScheduleConfig): Long {
        val now = Calendar.getInstance()

        // Days offset search (up to 7 days ahead)
        for (dayOffset in 0..7) {
            val candidateDay = Calendar.getInstance().apply {
                timeInMillis = now.timeInMillis
                add(Calendar.DAY_OF_YEAR, dayOffset)
            }

            if (schedule.repeatMode == "WEEKLY") {
                val dayOfWeek = candidateDay.get(Calendar.DAY_OF_WEEK)
                // Calendar.SUNDAY = 1, MONDAY = 2 ... SATURDAY = 7
                // Map to Persian week bit: Saturday (0), Sunday (1) .. Friday (6)
                val bitIndex = when (dayOfWeek) {
                    Calendar.SATURDAY -> 0
                    Calendar.SUNDAY -> 1
                    Calendar.MONDAY -> 2
                    Calendar.TUESDAY -> 3
                    Calendar.WEDNESDAY -> 4
                    Calendar.THURSDAY -> 5
                    Calendar.FRIDAY -> 6
                    else -> 0
                }
                val isDayActive = (schedule.weeklyDaysMask and (1 shl bitIndex)) != 0
                if (!isDayActive) {
                    continue
                }
            }

            // Window start and end for candidate day
            val windowStart = (candidateDay.clone() as Calendar).apply {
                set(Calendar.HOUR_OF_DAY, schedule.startHour)
                set(Calendar.MINUTE, schedule.startMinute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            val windowEnd = (candidateDay.clone() as Calendar).apply {
                set(Calendar.HOUR_OF_DAY, schedule.endHour)
                set(Calendar.MINUTE, schedule.endMinute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            // If end is before start, window wraps to next morning
            if (windowEnd.timeInMillis <= windowStart.timeInMillis) {
                windowEnd.add(Calendar.DAY_OF_YEAR, 1)
            }

            val currentMillis = now.timeInMillis
            if (dayOffset == 0) {
                // Today
                if (currentMillis < windowEnd.timeInMillis) {
                    val effectiveStart = if (currentMillis > windowStart.timeInMillis) {
                        currentMillis + 60_000L // 1 min from now
                    } else {
                        windowStart.timeInMillis
                    }
                    if (effectiveStart < windowEnd.timeInMillis) {
                        val delta = windowEnd.timeInMillis - effectiveStart
                        val randomOffset = if (delta > 0) Random.nextLong(delta) else 0L
                        return effectiveStart + randomOffset
                    }
                }
            } else {
                // Future active day
                val delta = windowEnd.timeInMillis - windowStart.timeInMillis
                val randomOffset = if (delta > 0) Random.nextLong(delta) else 0L
                return windowStart.timeInMillis + randomOffset
            }
        }

        // Fallback: 1 hour from now
        return now.timeInMillis + 3600_000L
    }
}
