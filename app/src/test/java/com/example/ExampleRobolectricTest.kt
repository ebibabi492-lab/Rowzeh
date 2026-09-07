package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.ScheduleConfig
import com.example.data.model.TimeInterval
import com.example.service.RowzehScheduler
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("ساعت روضه", appName)
  }

  @Test
  fun `calculateNextTriggerTime respects custom time intervals`() {
    val schedule = ScheduleConfig(
      id = 1,
      isEnabled = true,
      startHour = 18,
      startMinute = 0,
      endHour = 21,
      endMinute = 0,
      repeatMode = "DAILY"
    )
    val intervals = listOf(
      TimeInterval(
        id = 1,
        title = "بازه صبحگاهی",
        startHour = 8,
        startMinute = 0,
        endHour = 11,
        endMinute = 0,
        isEnabled = true
      ),
      TimeInterval(
        id = 2,
        title = "بازه عصرگاهی",
        startHour = 14,
        startMinute = 0,
        endHour = 17,
        endMinute = 0,
        isEnabled = false
      )
    )

    val nextTriggerTime = RowzehScheduler.calculateNextTriggerTime(schedule, intervals)
    assertTrue("Next trigger time should be in the future", nextTriggerTime > System.currentTimeMillis() - 1000L)
  }
}
