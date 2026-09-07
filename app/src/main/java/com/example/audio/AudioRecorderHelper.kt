package com.example.audio

import android.content.Context
import android.media.MediaMetadataRetriever
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import java.io.File

class AudioRecorderHelper(private val context: Context) {
    private var mediaRecorder: MediaRecorder? = null
    private var currentOutputFile: File? = null
    private var recordStartTime: Long = 0L

    var isRecording: Boolean = false
        private set

    fun startRecording(): File? {
        if (isRecording) return currentOutputFile

        val recordingsDir = File(context.filesDir, "recordings")
        if (!recordingsDir.exists()) {
            recordingsDir.mkdirs()
        }

        val outputFile = File(recordingsDir, "rec_${System.currentTimeMillis()}.m4a")
        currentOutputFile = outputFile

        val recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }

        try {
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            recorder.setAudioEncodingBitRate(128000)
            recorder.setAudioSamplingRate(44100)
            recorder.setOutputFile(outputFile.absolutePath)
            recorder.prepare()
            recorder.start()

            mediaRecorder = recorder
            isRecording = true
            recordStartTime = System.currentTimeMillis()
            return outputFile
        } catch (e: Exception) {
            Log.e("AudioRecorderHelper", "Failed to start recording", e)
            mediaRecorder?.release()
            mediaRecorder = null
            isRecording = false
            currentOutputFile = null
            return null
        }
    }

    fun stopRecording(): RecordResult? {
        if (!isRecording || mediaRecorder == null) return null

        return try {
            mediaRecorder?.stop()
            mediaRecorder?.release()
            mediaRecorder = null
            isRecording = false

            val file = currentOutputFile
            val durationSec = if (file != null && file.exists()) {
                getAudioDurationSeconds(file)
            } else {
                ((System.currentTimeMillis() - recordStartTime) / 1000).toInt()
            }

            if (file != null && file.exists() && file.length() > 0) {
                RecordResult(file.absolutePath, durationSec)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("AudioRecorderHelper", "Failed to stop recording", e)
            mediaRecorder?.release()
            mediaRecorder = null
            isRecording = false
            null
        }
    }

    fun cancelRecording() {
        try {
            if (isRecording) {
                mediaRecorder?.stop()
            }
        } catch (_: Exception) {}
        mediaRecorder?.release()
        mediaRecorder = null
        isRecording = false
        currentOutputFile?.delete()
        currentOutputFile = null
    }

    private fun getAudioDurationSeconds(file: File): Int {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(file.absolutePath)
            val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            val ms = time?.toLongOrNull() ?: 0L
            (ms / 1000).toInt().coerceAtLeast(1)
        } catch (_: Exception) {
            1
        } finally {
            retriever.release()
        }
    }

    data class RecordResult(
        val filePath: String,
        val durationSeconds: Int
    )
}
