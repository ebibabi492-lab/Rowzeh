package com.example.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.data.model.ScheduleConfig
import com.example.data.model.TimeInterval
import java.util.Calendar
import kotlin.random.Random

object RowzehScheduler {

    const val ACTION_ROWZEH_ALARM = "com.example.ACTION_ROWZEH_ALARM"
    private const val ALARM_REQUEST_CODE = 1001

    fun scheduleNextAlarm(
        context: Context,
        schedule: ScheduleConfig,
        intervals: List<TimeInterval>? = null
    ): Long {
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

        val nextTriggerMillis = calculateNextTriggerTime(schedule, intervals)

        val showIntent = Intent(context, com.example.MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingShow = PendingIntent.getActivity(
            context,
            ALARM_REQUEST_CODE + 1,
            showIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val canScheduleExact = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            try {
                alarmManager.canScheduleExactAlarms()
            } catch (_: Exception) {
                false
            }
        } else {
            true
        }

        try {
            if (canScheduleExact) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    val alarmClockInfo = AlarmManager.AlarmClockInfo(nextTriggerMillis, pendingShow)
                    alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
                } else {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, nextTriggerMillis, pendingIntent)
                }
                Log.d("RowzehScheduler", "Scheduled exact alarm clock for: $nextTriggerMillis")
            } else {
                // Inexact alarm allowed while idle when exact alarm permission is not granted
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        nextTriggerMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, nextTriggerMillis, pendingIntent)
                }
                Log.d("RowzehScheduler", "Scheduled alarm while idle (inexact) for: $nextTriggerMillis")
            }
        } catch (e: SecurityException) {
            Log.w("RowzehScheduler", "SecurityException scheduling exact alarm, falling back to setAndAllowWhileIdle: ${e.message}")
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        nextTriggerMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, nextTriggerMillis, pendingIntent)
                }
            } catch (fallbackEx: Exception) {
                Log.e("RowzehScheduler", "Fallback alarm scheduling failed", fallbackEx)
            }
        } catch (e: Exception) {
            Log.e("RowzehScheduler", "Error setting alarm clock", e)
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        nextTriggerMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, nextTriggerMillis, pendingIntent)
                }
            } catch (fallbackEx: Exception) {
                Log.e("RowzehScheduler", "Fallback alarm scheduling failed", fallbackEx)
            }
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

    fun calculateNextTriggerTime(
        schedule: ScheduleConfig,
        customIntervals: List<TimeInterval>? = null
    ): Long {
        val now = Calendar.getInstance()
        val activeIntervals = customIntervals?.filter { it.isEnabled }

        // If no custom intervals provided or none enabled, fallback to default window in ScheduleConfig
        val intervalsToUse: List<Pair<Pair<Int, Int>, Pair<Int, Int>>> = if (!activeIntervals.isNullOrEmpty()) {
            activeIntervals.map { (it.startHour to it.startMinute) to (it.endHour to it.endMinute) }
        } else {
            listOf((schedule.startHour to schedule.startMinute) to (schedule.endHour to schedule.endMinute))
        }

        // Days offset search (up to 7 days ahead)
        for (dayOffset in 0..7) {
            val candidateDay = Calendar.getInstance().apply {
                timeInMillis = now.timeInMillis
                add(Calendar.DAY_OF_YEAR, dayOffset)
            }

            if (schedule.repeatMode == "WEEKLY") {
                val dayOfWeek = candidateDay.get(Calendar.DAY_OF_WEEK)
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

            var earliestCandidate: Long? = null

            for (interval in intervalsToUse) {
                val startPair = interval.first
                val endPair = interval.second

                val windowStart = (candidateDay.clone() as Calendar).apply {
                    set(Calendar.HOUR_OF_DAY, startPair.first)
                    set(Calendar.MINUTE, startPair.second)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }

                val windowEnd = (candidateDay.clone() as Calendar).apply {
                    set(Calendar.HOUR_OF_DAY, endPair.first)
                    set(Calendar.MINUTE, endPair.second)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }

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
                            val candidate = effectiveStart + randomOffset
                            if (earliestCandidate == null || candidate < earliestCandidate) {
                                earliestCandidate = candidate
                            }
                        }
                    }
                } else {
                    // Future day
                    val delta = windowEnd.timeInMillis - windowStart.timeInMillis
                    val randomOffset = if (delta > 0) Random.nextLong(delta) else 0L
                    val candidate = windowStart.timeInMillis + randomOffset
                    if (earliestCandidate == null || candidate < earliestCandidate) {
                        earliestCandidate = candidate
                    }
                }
            }

            if (earliestCandidate != null) {
                return earliestCandidate
            }
        }

        // Fallback: 1 hour from now
        return now.timeInMillis + 3600_000L
    }
}
