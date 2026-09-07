package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.RowzehTrack
import com.example.ui.RowzehViewModel
import com.example.ui.theme.AlertCrimson
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldDark
import com.example.ui.theme.TurquoiseContainer
import com.example.ui.theme.TurquoiseLight
import com.example.ui.theme.TurquoisePrimary
import kotlinx.coroutines.delay
import java.util.Locale

// Convert English numbers to Persian digits for spiritual authentic Persian feel
fun toPersianDigits(input: String): String {
    val persianDigits = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
    return buildString {
        for (ch in input) {
            if (ch in '0'..'9') {
                append(persianDigits[ch - '0'])
            } else {
                append(ch)
            }
        }
    }
}

fun formatTime(hour: Int, minute: Int): String {
    val raw = String.format(Locale.US, "%02d:%02d", hour, minute)
    return toPersianDigits(raw)
}

fun formatSeconds(sec: Int): String {
    val m = sec / 60
    val s = sec % 60
    val raw = String.format(Locale.US, "%02d:%02d", m, s)
    return toPersianDigits(raw)
}

/**
 * Traditional Islamic Ornamental Divider with central geometric diamond
 */
@Composable
fun ShamsehDivider(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
        )
        Canvas(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .size(14.dp)
        ) {
            val center = Offset(size.width / 2, size.height / 2)
            val half = size.width / 2
            val path = Path().apply {
                moveTo(center.x, 0f)
                lineTo(size.width, center.y)
                lineTo(center.x, size.height)
                lineTo(0f, center.y)
                close()
            }
            drawPath(path, color = Color(0xFFD4AF37))
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
        )
    }
}

/**
 * Top Persian Header with artistic medallion icon and status
 */
@Composable
fun TraditionalHeader(
    isEnabled: Boolean,
    onQuickTestClick: () -> Unit,
    onSettingsClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("traditional_header_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.7f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
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
                    Image(
                        painter = painterResource(id = R.drawable.ic_rowzeh_art),
                        contentDescription = "آیکون ساعت روضه",
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(14.dp))
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "ساعت روضه",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 19.sp
                            )
                        )
                        Text(
                            text = if (isEnabled) "برنامه پخش تصادفی فعال است" else "برنامه غیرفعال است",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (isEnabled) TurquoisePrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Quick Test Button
                    Button(
                        onClick = onQuickTestClick,
                        modifier = Modifier.testTag("btn_quick_test"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldDark,
                            contentColor = Color.White
                        ),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "تست هشدار",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    if (onSettingsClick != null) {
                        Spacer(modifier = Modifier.width(6.dp))
                        IconButton(
                            onClick = onSettingsClick,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(TurquoiseContainer)
                                .testTag("btn_header_settings")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "تنظیمات بازه‌های زمانی",
                                tint = TurquoisePrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Schedule settings Card (Time Window, Repetition, Volume)
 */
@Composable
fun ScheduleConfigCard(
    isEnabled: Boolean,
    startHour: Int,
    startMinute: Int,
    endHour: Int,
    endMinute: Int,
    repeatMode: String,
    weeklyDaysMask: Int,
    volumePercent: Int,
    onToggleEnabled: (Boolean) -> Unit,
    onUpdateTimeWindow: (Int, Int, Int, Int) -> Unit,
    onUpdateRepeatMode: (String) -> Unit,
    onToggleWeeklyDay: (Int) -> Unit,
    onUpdateVolume: (Int) -> Unit,
    onOpenSettings: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var showTimePickerDialog by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("schedule_config_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Toggle Switch Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Alarm,
                        contentDescription = null,
                        tint = GoldDark,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "پخش خودکار و تصادفی",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "انتخاب تصادفی یک روضه در بازه معین",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Switch(
                    checked = isEnabled,
                    onCheckedChange = onToggleEnabled,
                    modifier = Modifier.testTag("switch_schedule_toggle"),
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = TurquoisePrimary
                    )
                )
            }

            ShamsehDivider(modifier = Modifier.padding(vertical = 10.dp))

            // Time Window Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showTimePickerDialog = true }
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "بازه زمانی پخش (ساعت شروع تا پایان)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "پخش در زمان نامشخص و غافلگیرکننده در این بازه",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = "${formatTime(startHour, startMinute)}  تا  ${formatTime(endHour, endMinute)}",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Repetition: Daily or Weekly
            Text(
                text = "انتخاب تکرار روضه",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Daily Button
                val isDaily = repeatMode == "DAILY"
                OutlinedButton(
                    onClick = { onUpdateRepeatMode("DAILY") },
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
                    Text(text = "تکرار روزانه", fontSize = 13.sp)
                }

                // Weekly Button
                val isWeekly = repeatMode == "WEEKLY"
                OutlinedButton(
                    onClick = { onUpdateRepeatMode("WEEKLY") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_repeat_weekly"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (isWeekly) TurquoisePrimary else Color.Transparent,
                        contentColor = if (isWeekly) Color.White else MaterialTheme.colorScheme.onSurface
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (isWeekly) TurquoisePrimary else MaterialTheme.colorScheme.outline
                    )
                ) {
                    Text(text = "تکرار هفتگی", fontSize = 13.sp)
                }
            }

            // If weekly, show Persian day chips (Saturday to Friday)
            AnimatedVisibility(visible = repeatMode == "WEEKLY") {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    Text(
                        text = "روزهای منتخب هفته:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val daysPersian = listOf(
                            "ش" to "شنبه",
                            "ی" to "یکشنبه",
                            "د" to "دوشنبه",
                            "س" to "سه‌شنبه",
                            "چ" to "چهارشنبه",
                            "پ" to "پنج‌شنبه",
                            "ج" to "جمعه"
                        )
                        daysPersian.forEachIndexed { index, pair ->
                            val isDaySelected = (weeklyDaysMask and (1 shl index)) != 0
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isDaySelected) GoldDark else MaterialTheme.colorScheme.surfaceVariant
                                    )
                                    .clickable { onToggleWeeklyDay(index) }
                                    .testTag("chip_day_$index"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = pair.first,
                                    fontSize = 13.sp,
                                    fontWeight = if (isDaySelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isDaySelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Volume Control Slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "میزان صدای پخش روضه",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${toPersianDigits(volumePercent.toString())} ٪",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldDark
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.VolumeDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
                Slider(
                    value = volumePercent.toFloat(),
                    onValueChange = { onUpdateVolume(it.toInt()) },
                    valueRange = 5f..100f,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                        .testTag("slider_volume"),
                    colors = SliderDefaults.colors(
                        thumbColor = GoldDark,
                        activeTrackColor = TurquoisePrimary
                    )
                )
                Icon(
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }

            if (onOpenSettings != null) {
                Spacer(modifier = Modifier.height(14.dp))
                OutlinedButton(
                    onClick = onOpenSettings,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_manage_intervals_page"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = TurquoisePrimary
                    ),
                    border = BorderStroke(1.dp, TurquoisePrimary)
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "صفحه تنظیمات و بازه‌های زمانی دلخواه",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    if (showTimePickerDialog) {
        TimeWindowPickerDialog(
            initialStartH = startHour,
            initialStartM = startMinute,
            initialEndH = endHour,
            initialEndM = endMinute,
            onDismiss = { showTimePickerDialog = false },
            onConfirm = { sH, sM, eH, eM ->
                showTimePickerDialog = false
                onUpdateTimeWindow(sH, sM, eH, eM)
            }
        )
    }
}

/**
 * Time Window Picker Dialog for Persian RTL
 */
@Composable
fun TimeWindowPickerDialog(
    initialStartH: Int,
    initialStartM: Int,
    initialEndH: Int,
    initialEndM: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int, Int, Int, Int) -> Unit
) {
    var startH by remember { mutableIntStateOf(initialStartH) }
    var startM by remember { mutableIntStateOf(initialStartM) }
    var endH by remember { mutableIntStateOf(initialEndH) }
    var endM by remember { mutableIntStateOf(initialEndM) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "تنظیم بازه زمانی پخش تصادفی",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "ساعت شروع بازه:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TimeNumberSpinner("ساعت", startH, 0..23) { startH = it }
                    Text(":", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    TimeNumberSpinner("دقیقه", startM, 0..59, step = 5) { startM = it }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "ساعت پایان بازه:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TimeNumberSpinner("ساعت", endH, 0..23) { endH = it }
                    Text(":", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    TimeNumberSpinner("دقیقه", endM, 0..59, step = 5) { endM = it }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "در این بازه، یک صوت به شکل تصادفی پس از هشدار پخش خواهد شد.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(startH, startM, endH, endM) },
                colors = ButtonDefaults.buttonColors(containerColor = TurquoisePrimary)
            ) {
                Text("ثبت و ذخیره")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("انصراف")
            }
        }
    )
}

@Composable
fun TimeNumberSpinner(
    label: String,
    value: Int,
    range: IntRange,
    step: Int = 1,
    onValueChange: (Int) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
            .padding(horizontal = 6.dp, vertical = 4.dp)
    ) {
        IconButton(
            onClick = {
                val next = if (value - step < range.first) range.last else value - step
                onValueChange(next)
            },
            modifier = Modifier.size(32.dp)
        ) {
            Text("-", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Text(
            text = toPersianDigits(String.format(Locale.US, "%02d", value)),
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        IconButton(
            onClick = {
                val next = if (value + step > range.last) range.first else value + step
                onValueChange(next)
            },
            modifier = Modifier.size(32.dp)
        ) {
            Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

/**
 * Individual Audio Track Item
 */
@Composable
fun AudioTrackCard(
    track: RowzehTrack,
    isPlayingThis: Boolean,
    onToggleInclusion: () -> Unit,
    onPlayPreview: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("audio_track_${track.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isPlayingThis) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            1.dp,
            if (isPlayingThis) TurquoisePrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox for inclusion in random playback
            Checkbox(
                checked = track.isIncludedInRandom,
                onCheckedChange = { onToggleInclusion() },
                colors = CheckboxDefaults.colors(
                    checkedColor = TurquoisePrimary,
                    checkmarkColor = Color.White
                ),
                modifier = Modifier.testTag("checkbox_track_${track.id}")
            )

            // Play/Stop preview button
            IconButton(
                onClick = onPlayPreview,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (isPlayingThis) TurquoisePrimary else MaterialTheme.colorScheme.surfaceVariant)
                    .testTag("btn_play_track_${track.id}")
            ) {
                Icon(
                    imageVector = if (isPlayingThis) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlayingThis) "توقف" else "پخش",
                    tint = if (isPlayingThis) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Track title & info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = track.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = track.speaker,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Text(
                        text = " • ${formatSeconds(track.durationSeconds)}",
                        fontSize = 11.sp,
                        color = GoldDark,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Track Type Badge (Built-in / Recorded / Uploaded)
            val badgeText = when {
                track.isBuiltIn -> "پیش‌فرض"
                track.isRecorded -> "ضبطی"
                else -> "فایل شخصی"
            }
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.padding(horizontal = 6.dp)
            ) {
                Text(
                    text = badgeText,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Delete button (allowed for user tracks)
            if (!track.isBuiltIn) {
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("btn_delete_track_${track.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "حذف",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

/**
 * Pre-Alert Dialog ("زمان روضه")
 * Prominently warns the user before rowzeh playback starts!
 */
@Composable
fun RowzehPreAlertDialog(
    trackTitle: String,
    countdownSec: Int,
    onDismiss: () -> Unit,
    onConfirmPlay: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.NotificationsActive,
                    contentDescription = null,
                    tint = AlertCrimson,
                    modifier = Modifier
                        .size(28.dp)
                        .scale(scale)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "زمان روضه",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = AlertCrimson
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "زمان معین پخش روضه فرا رسیده است.",
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "«$trackTitle»",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = TurquoisePrimary
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "برای شروع دلنشین زمزمه و ذکر اهل بیت (ع) آماده‌اید؟",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirmPlay,
                colors = ButtonDefaults.buttonColors(containerColor = TurquoisePrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("btn_confirm_rowzeh_alert")
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("پخش روضه")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("btn_dismiss_rowzeh_alert")
            ) {
                Text("انصراف")
            }
        },
        shape = RoundedCornerShape(22.dp)
    )
}

/**
 * Recording Dialog with timer and visual feedback
 */
@Composable
fun VoiceRecordingDialog(
    isRecording: Boolean,
    durationSec: Int,
    onStopRecording: () -> Unit,
    onCancelRecording: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_record")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(700),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    AlertDialog(
        onDismissRequest = onCancelRecording,
        title = {
            Text(
                text = "ضبط روضه شخصی",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(AlertCrimson.copy(alpha = pulseAlpha)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "ضبط",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "در حال ضبط صدای روضه و مداحی...",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = formatSeconds(durationSec),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = AlertCrimson
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onStopRecording,
                colors = ButtonDefaults.buttonColors(containerColor = TurquoisePrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("btn_stop_recording")
            ) {
                Icon(Icons.Default.Stop, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("توقف و ذخیره")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onCancelRecording,
                modifier = Modifier.testTag("btn_cancel_recording")
            ) {
                Text("لغو")
            }
        },
        shape = RoundedCornerShape(22.dp)
    )
}

/**
 * Dialog to name and save newly recorded track
 */
@Composable
fun SaveRecordedAudioDialog(
    initialDurationSec: Int,
    onDismiss: () -> Unit,
    onSave: (title: String, speaker: String) -> Unit
) {
    var title by remember { mutableStateOf("روضه شخصی") }
    var speaker by remember { mutableStateOf("صدای ضبط‌شده") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "ذخیره فایل صوتی ضبط‌شده",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "مدت زمان ضبط: ${formatSeconds(initialDurationSec)}",
                    fontSize = 12.sp,
                    color = GoldDark,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("عنوان روضه یا مرثیه") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_recorded_title")
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = speaker,
                    onValueChange = { speaker = it },
                    label = { Text("نام مداح یا ذاکر") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_recorded_speaker")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(title, speaker) },
                colors = ButtonDefaults.buttonColors(containerColor = TurquoisePrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("btn_save_recorded_track")
            ) {
                Text("افزودن به لیست")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("لغو")
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}

/**
 * Bottom Floating Active Playback Controller
 */
@Composable
fun ActivePlaybackBottomBar(
    title: String,
    speaker: String,
    isPlaying: Boolean,
    onPauseResume: () -> Unit,
    onStop: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("active_playback_bar"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        border = BorderStroke(1.5.dp, GoldAccent),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(TurquoisePrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = speaker,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onPauseResume,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(TurquoisePrimary)
                            .testTag("btn_bottom_play_pause")
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "توقف موقت" else "ادامه",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = onStop,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .testTag("btn_bottom_stop")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stop,
                            contentDescription = "توقف کامل",
                            tint = AlertCrimson,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
