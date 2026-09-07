package com.example.ui.settings

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ScheduleConfig
import com.example.data.model.TimeInterval
import com.example.ui.RowzehViewModel
import com.example.ui.components.toPersianDigits
import com.example.ui.theme.AlertCrimson
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.TurquoiseContainer
import com.example.ui.theme.TurquoisePrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun RowzehSettingsScreen(
    viewModel: RowzehViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val schedule by viewModel.scheduleConfig.collectAsStateWithLifecycle()
    val intervals by viewModel.allIntervals.collectAsStateWithLifecycle()

    val safeSchedule = schedule ?: ScheduleConfig()

    // Dialog state for adding or editing an interval
    var intervalToEdit by remember { mutableStateOf<TimeInterval?>(null) }
    var isAddingNewInterval by remember { mutableStateOf(false) }

    BackHandler {
        onNavigateBack()
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            modifier = modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .testTag("rowzeh_settings_screen"),
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "تنظیمات بازه‌های زمانی",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 19.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                            Text(
                                text = "مدیریت ساعات و زمان‌بندی تصادفی روضه",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier.testTag("btn_settings_back")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "بازگشت به صفحه اصلی",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .testTag("settings_scroll_column"),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Master Enable Card & Next Alarm Info
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("settings_master_toggle_card"),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (safeSchedule.isEnabled) TurquoiseContainer.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (safeSchedule.isEnabled) TurquoisePrimary else MaterialTheme.colorScheme.outline
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .background(if (safeSchedule.isEnabled) TurquoisePrimary else MaterialTheme.colorScheme.surfaceVariant),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Schedule,
                                            contentDescription = null,
                                            tint = if (safeSchedule.isEnabled) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "پخش تصادفی روضه",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = if (safeSchedule.isEnabled) "فعال در بازه‌های زمانی مشخص شده" else "غیرفعال است",
                                            fontSize = 12.sp,
                                            color = if (safeSchedule.isEnabled) TurquoisePrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                Switch(
                                    checked = safeSchedule.isEnabled,
                                    onCheckedChange = { viewModel.toggleScheduleEnabled(it) },
                                    modifier = Modifier.testTag("switch_master_schedule"),
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = TurquoisePrimary
                                    )
                                )
                            }

                            if (safeSchedule.isEnabled && safeSchedule.nextScheduledTimestamp > 0L) {
                                Spacer(modifier = Modifier.height(14.dp))
                                val format = SimpleDateFormat("HH:mm", Locale.getDefault())
                                val timeStr = format.format(Date(safeSchedule.nextScheduledTimestamp))
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.surface,
                                    border = BorderStroke(1.dp, GoldDark.copy(alpha = 0.5f))
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.NotificationsActive,
                                            contentDescription = null,
                                            tint = GoldDark,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "نوبت پخش تصادفی بعدی: حدود ساعت ${toPersianDigits(timeStr)}",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = MaterialTheme.colorScheme.onSurface,
                                                fontWeight = FontWeight.Medium
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 2. Custom Time Intervals Section Header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "بازه‌های زمانی دلخواه",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            )
                            Text(
                                text = "پخش تصادفی تنها در این بازه‌ها انجام می‌شود",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }

                        Button(
                            onClick = { isAddingNewInterval = true },
                            modifier = Modifier.testTag("btn_add_custom_interval"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = TurquoisePrimary,
                                contentColor = Color.White
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "افزودن بازه", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // 3. List of Custom Time Intervals
                if (intervals.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "هیچ بازه زمانی فعالی ثبت نشده است",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = { isAddingNewInterval = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = TurquoisePrimary)
                                ) {
                                    Text("افزودن اولین بازه زمانی")
                                }
                            }
                        }
                    }
                } else {
                    items(intervals, key = { it.id }) { interval ->
                        TimeIntervalCard(
                            interval = interval,
                            onToggle = { viewModel.toggleIntervalEnabled(interval) },
                            onEdit = { intervalToEdit = interval },
                            onDelete = {
                                viewModel.deleteInterval(interval)
                                Toast.makeText(context, "بازه زمانی حذف شد", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }

                // 4. Repetition Settings Card
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("settings_repetition_card"),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.7f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "الگوی تکرار برنامه",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                val isDaily = safeSchedule.repeatMode == "DAILY"
                                OutlinedButton(
                                    onClick = { viewModel.updateRepeatMode("DAILY") },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("btn_repeat_daily"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = if (isDaily) TurquoisePrimary else Color.Transparent,
                                        contentColor = if (isDaily) Color.White else MaterialTheme.colorScheme.onSurface
                                    ),
                                    border = BorderStroke(
                                        1.dp,
                                        if (isDaily) TurquoisePrimary else MaterialTheme.colorScheme.outline
                                    )
                                ) {
                                    Text(
                                        text = "روزانه (هر روز)",
                                        fontWeight = if (isDaily) FontWeight.Bold else FontWeight.Normal
                                    )
                                }

                                OutlinedButton(
                                    onClick = { viewModel.updateRepeatMode("WEEKLY") },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("btn_repeat_weekly"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = if (!isDaily) TurquoisePrimary else Color.Transparent,
                                        contentColor = if (!isDaily) Color.White else MaterialTheme.colorScheme.onSurface
                                    ),
                                    border = BorderStroke(
                                        1.dp,
                                        if (!isDaily) TurquoisePrimary else MaterialTheme.colorScheme.outline
                                    )
                                ) {
                                    Text(
                                        text = "هفتگی (روزهای خاص)",
                                        fontWeight = if (!isDaily) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }

                            AnimatedVisibility(visible = safeSchedule.repeatMode == "WEEKLY") {
                                Column(modifier = Modifier.padding(top = 14.dp)) {
                                    Text(
                                        text = "انتخاب روزهای فعال:",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))

                                    val days = listOf("شنبه", "۱شنبه", "۲شنبه", "۳شنبه", "۴شنبه", "۵شنبه", "جمعه")
                                    FlowRow(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        days.forEachIndexed { index, name ->
                                            val isSelected = (safeSchedule.weeklyDaysMask and (1 shl index)) != 0
                                            FilterChip(
                                                selected = isSelected,
                                                onClick = { viewModel.toggleWeeklyDay(index) },
                                                label = {
                                                    Text(
                                                        text = name,
                                                        fontSize = 12.sp,
                                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                                    )
                                                },
                                                colors = FilterChipDefaults.filterChipColors(
                                                    selectedContainerColor = GoldDark,
                                                    selectedLabelColor = Color.White
                                                ),
                                                modifier = Modifier.testTag("chip_day_$index")
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 5. Volume & Pre-Alert Info Card
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("settings_volume_card"),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.7f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.VolumeUp,
                                        contentDescription = null,
                                        tint = TurquoisePrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "میزان بلندی صدای پخش",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                }
                                Text(
                                    text = "${toPersianDigits(safeSchedule.volumePercent.toString())}٪",
                                    fontWeight = FontWeight.Bold,
                                    color = TurquoisePrimary
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VolumeDown,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                                Slider(
                                    value = safeSchedule.volumePercent.toFloat(),
                                    onValueChange = { viewModel.updateVolume(it.toInt()) },
                                    valueRange = 10f..100f,
                                    steps = 17,
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = 8.dp)
                                        .testTag("slider_settings_volume"),
                                    colors = SliderDefaults.colors(
                                        thumbColor = TurquoisePrimary,
                                        activeTrackColor = TurquoisePrimary,
                                        inactiveTrackColor = TurquoiseContainer
                                    )
                                )
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = null,
                                    tint = TurquoisePrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Alert notice
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.NotificationsActive,
                                        contentDescription = null,
                                        tint = AlertCrimson,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "پیش از پخش خودکار روضه، هشدار «زمان روضه» با لرزش و اعلان صوتی ظاهر می‌شود.",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontSize = 11.5.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                // 6. Save and Confirm Button
                item {
                    Button(
                        onClick = {
                            Toast.makeText(context, "تنظیمات و بازه‌های زمانی با موفقیت ذخیره شدند", Toast.LENGTH_SHORT).show()
                            onNavigateBack()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("btn_save_settings"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TurquoisePrimary,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ذخیره و بازگشت به صفحه اصلی",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        // Dialog for adding or editing an interval
        if (isAddingNewInterval || intervalToEdit != null) {
            IntervalEditDialog(
                initialInterval = intervalToEdit,
                onDismiss = {
                    isAddingNewInterval = false
                    intervalToEdit = null
                },
                onSave = { title, startH, startM, endH, endM ->
                    val editing = intervalToEdit
                    if (editing != null) {
                        viewModel.updateInterval(
                            editing.copy(
                                title = title,
                                startHour = startH,
                                startMinute = startM,
                                endHour = endH,
                                endMinute = endM
                            )
                        )
                        Toast.makeText(context, "بازه زمانی به‌روزرسانی شد", Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.addInterval(
                            title = title,
                            startHour = startH,
                            startMinute = startM,
                            endHour = endH,
                            endMinute = endM
                        )
                        Toast.makeText(context, "بازه زمانی جدید افزوده شد", Toast.LENGTH_SHORT).show()
                    }
                    isAddingNewInterval = false
                    intervalToEdit = null
                }
            )
        }
    }
}

/**
 * Custom Card component representing a single Time Interval
 */
@Composable
private fun TimeIntervalCard(
    interval: TimeInterval,
    onToggle: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val startStr = String.format("%02d:%02d", interval.startHour, interval.startMinute)
    val endStr = String.format("%02d:%02d", interval.endHour, interval.endMinute)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("interval_card_${interval.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (interval.isEnabled) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        ),
        border = BorderStroke(
            1.dp,
            if (interval.isEnabled) TurquoisePrimary.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (interval.isEnabled) 2.dp else 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(if (interval.isEnabled) TurquoiseContainer else MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = if (interval.isEnabled) TurquoisePrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = interval.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = if (interval.isEnabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "از ساعت ${toPersianDigits(startStr)} تا ${toPersianDigits(endStr)}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = if (interval.isEnabled) TurquoisePrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(
                    checked = interval.isEnabled,
                    onCheckedChange = { onToggle() },
                    modifier = Modifier.testTag("switch_interval_${interval.id}"),
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = TurquoisePrimary
                    )
                )

                Spacer(modifier = Modifier.width(4.dp))

                IconButton(
                    onClick = onEdit,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("btn_edit_interval_${interval.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "ویرایش بازه",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("btn_delete_interval_${interval.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "حذف بازه",
                        tint = AlertCrimson,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

/**
 * Dialog for creating or editing a custom time interval
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun IntervalEditDialog(
    initialInterval: TimeInterval?,
    onDismiss: () -> Unit,
    onSave: (title: String, startH: Int, startM: Int, endH: Int, endM: Int) -> Unit
) {
    var title by remember { mutableStateOf(initialInterval?.title ?: "بازه زمانی دلخواه") }
    var startHour by remember { mutableIntStateOf(initialInterval?.startHour ?: 18) }
    var startMinute by remember { mutableIntStateOf(initialInterval?.startMinute ?: 0) }
    var endHour by remember { mutableIntStateOf(initialInterval?.endHour ?: 21) }
    var endMinute by remember { mutableIntStateOf(initialInterval?.endMinute ?: 0) }

    // Quick presets
    val presets = listOf(
        Triple("سحر و صبحگاه", 4 to 0, 7 to 0),
        Triple("صبح و پیش‌ازظهر", 8 to 0, 11 to 0),
        Triple("ظهر و بعدازظهر", 12 to 0, 15 to 0),
        Triple("غروب و شامگاه", 18 to 0, 21 to 0),
        Triple("پایان شب", 21 to 30, 23 to 59)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("dialog_interval_edit"),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = null,
                    tint = TurquoisePrimary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (initialInterval != null) "ویرایش بازه زمانی" else "افزودن بازه زمانی دلخواه",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Preset chips
                Text(
                    text = "الگوهای سریع بازه:",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    presets.forEach { (pTitle, pStart, pEnd) ->
                        FilterChip(
                            selected = startHour == pStart.first && endHour == pEnd.first,
                            onClick = {
                                title = pTitle
                                startHour = pStart.first
                                startMinute = pStart.second
                                endHour = pEnd.first
                                endMinute = pEnd.second
                            },
                            label = { Text(text = pTitle, fontSize = 11.sp) }
                        )
                    }
                }

                // Title Input
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("عنوان بازه زمانی") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_interval_title"),
                    shape = RoundedCornerShape(12.dp)
                )

                // Start Time
                Text(
                    text = "ساعت شروع: ${toPersianDigits(String.format("%02d:%02d", startHour, startMinute))}",
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    color = TurquoisePrimary
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "ساعت (${toPersianDigits(startHour.toString())})", fontSize = 11.sp)
                        Slider(
                            value = startHour.toFloat(),
                            onValueChange = { startHour = it.toInt() },
                            valueRange = 0f..23f,
                            steps = 22,
                            modifier = Modifier.testTag("slider_start_hour"),
                            colors = SliderDefaults.colors(thumbColor = TurquoisePrimary, activeTrackColor = TurquoisePrimary)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "دقیقه (${toPersianDigits(startMinute.toString())})", fontSize = 11.sp)
                        Slider(
                            value = startMinute.toFloat(),
                            onValueChange = { startMinute = (it.toInt() / 5) * 5 },
                            valueRange = 0f..55f,
                            steps = 10,
                            modifier = Modifier.testTag("slider_start_minute"),
                            colors = SliderDefaults.colors(thumbColor = TurquoisePrimary, activeTrackColor = TurquoisePrimary)
                        )
                    }
                }

                // End Time
                Text(
                    text = "ساعت پایان: ${toPersianDigits(String.format("%02d:%02d", endHour, endMinute))}",
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    color = TurquoisePrimary
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "ساعت (${toPersianDigits(endHour.toString())})", fontSize = 11.sp)
                        Slider(
                            value = endHour.toFloat(),
                            onValueChange = { endHour = it.toInt() },
                            valueRange = 0f..23f,
                            steps = 22,
                            modifier = Modifier.testTag("slider_end_hour"),
                            colors = SliderDefaults.colors(thumbColor = TurquoisePrimary, activeTrackColor = TurquoisePrimary)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "دقیقه (${toPersianDigits(endMinute.toString())})", fontSize = 11.sp)
                        Slider(
                            value = endMinute.toFloat(),
                            onValueChange = { endMinute = (it.toInt() / 5) * 5 },
                            valueRange = 0f..55f,
                            steps = 10,
                            modifier = Modifier.testTag("slider_end_minute"),
                            colors = SliderDefaults.colors(thumbColor = TurquoisePrimary, activeTrackColor = TurquoisePrimary)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isBlank()) title = "بازه زمانی دلخواه"
                    onSave(title, startHour, startMinute, endHour, endMinute)
                },
                colors = ButtonDefaults.buttonColors(containerColor = TurquoisePrimary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("btn_save_dialog_interval")
            ) {
                Text("ذخیره بازه")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("btn_cancel_dialog_interval")
            ) {
                Text("انصراف")
            }
        }
    )
}
