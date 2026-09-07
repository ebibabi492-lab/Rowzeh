package com.example.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ScheduleConfig
import com.example.service.RowzehPlaybackService
import com.example.ui.components.ActivePlaybackBottomBar
import com.example.ui.components.AudioTrackCard
import com.example.ui.components.DeleteTrackConfirmDialog
import com.example.ui.components.EmptyListAlertDialog
import com.example.ui.components.RowzehPreAlertDialog
import com.example.ui.components.SaveRecordedAudioDialog
import com.example.ui.components.ScheduleConfigCard
import com.example.ui.components.ShamsehDivider
import com.example.ui.components.TraditionalHeader
import com.example.ui.components.VoiceRecordingDialog
import com.example.ui.components.toPersianDigits
import com.example.ui.theme.GoldDark
import com.example.ui.theme.TurquoisePrimary

@Composable
fun RowzehMainScreen(
    viewModel: RowzehViewModel,
    onNavigateToSettings: () -> Unit = {},
    onNavigateToGuide: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val tracks by viewModel.allTracks.collectAsStateWithLifecycle()
    val schedule by viewModel.scheduleConfig.collectAsStateWithLifecycle()
    val playingTrack by viewModel.playingState.collectAsStateWithLifecycle()
    val isRecording by viewModel.isRecording.collectAsStateWithLifecycle()
    val recordDurationSec by viewModel.recordedDurationSec.collectAsStateWithLifecycle()
    val pendingRecordedResult by viewModel.pendingRecordedResult.collectAsStateWithLifecycle()
    val rowzehAlert by viewModel.showRowzehAlert.collectAsStateWithLifecycle()
    val trackToDelete by viewModel.trackToDelete.collectAsStateWithLifecycle()
    val emptyListAlert by viewModel.emptyListAlert.collectAsStateWithLifecycle()

    var filterMode by remember { mutableStateOf("ALL") } // "ALL", "RECORDED", "UPLOADED"

    // File picker launcher
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            viewModel.importAudioFile(uri)
            Toast.makeText(context, "فایل صوتی شخصی اضافه شد", Toast.LENGTH_SHORT).show()
        }
    }

    // Permission launcher for audio recording
    val recordPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val started = viewModel.startVoiceRecording()
            if (!started) {
                Toast.makeText(context, "خطا در شروع ضبط صدا", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "برای ضبط صدا مجوز میکروفون لازم است", Toast.LENGTH_SHORT).show()
        }
    }

    // Notification permission launcher for Android 13+
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    // Enforce RTL for Persian layout
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            modifier = modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing),
            containerColor = MaterialTheme.colorScheme.background,
            bottomBar = {
                AnimatedVisibility(
                    visible = playingTrack != null,
                    enter = slideInVertically(initialOffsetY = { it }),
                    exit = slideOutVertically(targetOffsetY = { it })
                ) {
                    playingTrack?.let { state ->
                        ActivePlaybackBottomBar(
                            title = state.title,
                            speaker = state.speaker,
                            isPlaying = state.isPlaying,
                            onPauseResume = {
                                if (state.isPlaying) {
                                    val intent = android.content.Intent(context, RowzehPlaybackService::class.java).apply {
                                        action = RowzehPlaybackService.ACTION_PAUSE
                                    }
                                    context.startService(intent)
                                } else {
                                    val intent = android.content.Intent(context, RowzehPlaybackService::class.java).apply {
                                        action = RowzehPlaybackService.ACTION_RESUME
                                    }
                                    context.startService(intent)
                                }
                            },
                            onStop = { viewModel.stopPlayback() },
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .windowInsetsPadding(WindowInsets.navigationBars)
                        )
                    }
                }
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .testTag("main_scroll_column"),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. Top Spiritual Header
                item {
                    val isEnabled = schedule?.isEnabled ?: true
                    TraditionalHeader(
                        isEnabled = isEnabled,
                        onQuickTestClick = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                if (ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.POST_NOTIFICATIONS
                                    ) != PackageManager.PERMISSION_GRANTED
                                ) {
                                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                }
                            }
                            viewModel.triggerInstantRandomTest()
                        },
                        onSettingsClick = onNavigateToSettings,
                        onGuideClick = onNavigateToGuide
                    )
                }

                // 2. Schedule & Time Range Configuration Card
                item {
                    val safeSchedule = schedule ?: ScheduleConfig()
                    ScheduleConfigCard(
                        isEnabled = safeSchedule.isEnabled,
                        startHour = safeSchedule.startHour,
                        startMinute = safeSchedule.startMinute,
                        endHour = safeSchedule.endHour,
                        endMinute = safeSchedule.endMinute,
                        repeatMode = safeSchedule.repeatMode,
                        weeklyDaysMask = safeSchedule.weeklyDaysMask,
                        volumePercent = safeSchedule.volumePercent,
                        onToggleEnabled = { viewModel.toggleScheduleEnabled(it) },
                        onUpdateTimeWindow = { sH, sM, eH, eM ->
                            viewModel.updateTimeWindow(sH, sM, eH, eM)
                        },
                        onUpdateRepeatMode = { viewModel.updateRepeatMode(it) },
                        onToggleWeeklyDay = { viewModel.toggleWeeklyDay(it) },
                        onUpdateVolume = { viewModel.updateVolume(it) },
                        onOpenSettings = onNavigateToSettings
                    )
                }

                // 3. Action Buttons: Upload Audio File & Record Voice
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Upload File Button
                        OutlinedButton(
                            onClick = { filePickerLauncher.launch("audio/*") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_upload_audio"),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, TurquoisePrimary),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = TurquoisePrimary
                            ),
                            contentPadding = PaddingValues(vertical = 12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.UploadFile,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "بارگذاری فایل صوتی", fontSize = 12.sp)
                        }

                        // Record Voice Button
                        Button(
                            onClick = {
                                if (ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.RECORD_AUDIO
                                    ) == PackageManager.PERMISSION_GRANTED
                                ) {
                                    viewModel.startVoiceRecording()
                                } else {
                                    recordPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_record_audio"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = TurquoisePrimary,
                                contentColor = Color.White
                            ),
                            contentPadding = PaddingValues(vertical = 12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "ضبط صدای روضه", fontSize = 12.sp)
                        }
                    }
                }

                // 4. Playlist Header & Counters
                item {
                    val total = tracks.size
                    val included = tracks.count { it.isIncludedInRandom }
                    val recordedCount = tracks.count { it.isRecorded }
                    val uploadedCount = tracks.count { !it.isRecorded }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "فهرست روضه‌ها و نواهای شما",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            if (total > 0) {
                                Text(
                                    text = "${toPersianDigits(included.toString())} از ${toPersianDigits(total.toString())} در چرخه پخش",
                                    fontSize = 12.sp,
                                    color = GoldDark,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // Filter Chips when tracks exist
                        if (total > 0) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilterChip(
                                    selected = filterMode == "ALL",
                                    onClick = { filterMode = "ALL" },
                                    label = { Text("همه (${toPersianDigits(total.toString())})", fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = TurquoisePrimary,
                                        selectedLabelColor = Color.White
                                    )
                                )
                                FilterChip(
                                    selected = filterMode == "RECORDED",
                                    onClick = { filterMode = "RECORDED" },
                                    label = { Text("🎙️ ضبطی (${toPersianDigits(recordedCount.toString())})", fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = TurquoisePrimary,
                                        selectedLabelColor = Color.White
                                    )
                                )
                                FilterChip(
                                    selected = filterMode == "UPLOADED",
                                    onClick = { filterMode = "UPLOADED" },
                                    label = { Text("📁 بارگذاری (${toPersianDigits(uploadedCount.toString())})", fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = TurquoisePrimary,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }
                }

                // 5. Audio Tracks List or Empty State
                if (tracks.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp)
                                .testTag("empty_tracks_card"),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ),
                            border = BorderStroke(1.dp, TurquoisePrimary.copy(alpha = 0.3f))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.QueueMusic,
                                    contentDescription = null,
                                    tint = TurquoisePrimary,
                                    modifier = Modifier.size(54.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "هنوز روضه‌ای اضافه نکرده‌اید",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "فایل‌های پیش‌فرض طبق درخواست شما حذف شدند. برای پخش اتفاقی روضه در ساعات تعیین‌شده، لطفاً با دکمه‌های زیر اولین فایل صوتی خود را ضبط یا بارگذاری نمایید.",
                                    fontSize = 13.sp,
                                    lineHeight = 22.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = { filePickerLauncher.launch("audio/*") },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(12.dp),
                                        border = BorderStroke(1.dp, TurquoisePrimary),
                                        contentPadding = PaddingValues(vertical = 10.dp)
                                    ) {
                                        Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("بارگذاری فایل", fontSize = 12.sp)
                                    }
                                    Button(
                                        onClick = {
                                            if (ContextCompat.checkSelfPermission(
                                                    context,
                                                    Manifest.permission.RECORD_AUDIO
                                                ) == PackageManager.PERMISSION_GRANTED
                                            ) {
                                                viewModel.startVoiceRecording()
                                            } else {
                                                recordPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                            }
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = TurquoisePrimary),
                                        contentPadding = PaddingValues(vertical = 10.dp)
                                    ) {
                                        Icon(Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("ضبط صدا", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    val filteredTracks = when (filterMode) {
                        "RECORDED" -> tracks.filter { it.isRecorded }
                        "UPLOADED" -> tracks.filter { !it.isRecorded }
                        else -> tracks
                    }

                    if (filteredTracks.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 20.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "هیچ موردی در این دسته‌بندی یافت نشد.",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                    } else {
                        items(filteredTracks, key = { it.id }) { track ->
                            val isPlayingThis = playingTrack?.filePath == track.filePath && playingTrack?.isPlaying == true
                            AudioTrackCard(
                                track = track,
                                isPlayingThis = isPlayingThis,
                                onToggleInclusion = { viewModel.toggleTrackInclusion(track) },
                                onPlayPreview = {
                                    if (isPlayingThis) {
                                        viewModel.stopPlayback()
                                    } else {
                                        viewModel.playTrack(track)
                                    }
                                },
                                onDelete = { viewModel.requestDeleteTrack(track) }
                            )
                        }
                    }
                }

                // Extra spacer at bottom to ensure floating player does not hide last item
                item {
                    Spacer(modifier = Modifier.height(72.dp))
                }
            }
        }

        // Pre-Alert Dialog ("زمان روضه")
        rowzehAlert?.let { alertData ->
            RowzehPreAlertDialog(
                trackTitle = alertData.track.title,
                countdownSec = alertData.countdownSeconds,
                isPlaying = (playingTrack?.filePath == alertData.track.filePath && playingTrack?.isPlaying == true),
                onDismiss = { viewModel.dismissRowzehAlert() },
                onConfirmPlay = { viewModel.confirmPlayFromAlert() },
                onStopPlay = {
                    viewModel.stopPlayback()
                    viewModel.dismissRowzehAlert()
                }
            )
        }

        // Delete track confirmation dialog
        trackToDelete?.let { track ->
            DeleteTrackConfirmDialog(
                trackTitle = track.title,
                onDismiss = { viewModel.cancelDeleteTrack() },
                onConfirmDelete = {
                    viewModel.confirmDeleteTrack()
                    Toast.makeText(context, "روضه با موفقیت حذف شد", Toast.LENGTH_SHORT).show()
                }
            )
        }

        // Empty list alert when user triggers test or alarm without tracks
        if (emptyListAlert) {
            EmptyListAlertDialog(
                onDismiss = { viewModel.dismissEmptyListAlert() }
            )
        }

        // Recording in progress Dialog
        if (isRecording) {
            VoiceRecordingDialog(
                isRecording = isRecording,
                durationSec = recordDurationSec,
                onStopRecording = { viewModel.stopVoiceRecording() },
                onCancelRecording = { viewModel.cancelVoiceRecording() }
            )
        }

        // Save recorded audio file Dialog
        pendingRecordedResult?.let { result ->
            SaveRecordedAudioDialog(
                initialDurationSec = result.durationSeconds,
                onDismiss = { viewModel.dismissPendingRecording() },
                onSave = { title, speaker ->
                    viewModel.saveRecordedTrack(title, speaker)
                    Toast.makeText(context, "روضه ضبط‌شده به فهرست اضافه شد", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}
