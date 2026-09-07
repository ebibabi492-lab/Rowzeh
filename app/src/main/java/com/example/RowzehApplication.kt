package com.example

import android.app.Application
import com.example.data.local.AppDatabase
import com.example.data.repository.RowzehRepository
import com.example.service.RowzehNotificationHelper
import com.example.service.RowzehScheduler
import com.example.widget.RowzehAppWidgetProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RowzehApplication : Application() {

    lateinit var repository: RowzehRepository
        private set

    override fun onCreate() {
        super.onCreate()

        val db = AppDatabase.getInstance(this)
        repository = RowzehRepository(db.trackDao(), db.scheduleDao())

        // Create notification channels
        RowzehNotificationHelper.createNotificationChannels(this)

        // Seed default samples and schedule
        CoroutineScope(Dispatchers.IO).launch {
            repository.initializeDefaultsIfEmpty(this@RowzehApplication)
            val schedule = repository.getScheduleSync()
            if (schedule != null && schedule.isEnabled) {
                val nextTime = RowzehScheduler.scheduleNextAlarm(this@RowzehApplication, schedule)
                repository.updateNextSchedule(nextTime)
            }
            RowzehAppWidgetProvider.updateAllWidgets(this@RowzehApplication)
        }
    }
}
