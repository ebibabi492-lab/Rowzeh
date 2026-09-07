package com.example.audio

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import com.example.data.model.RowzehTrack
import java.io.File
import java.io.FileOutputStream

object AudioFileImporter {

    fun importAudioFromUri(context: Context, uri: Uri): RowzehTrack? {
        return try {
            val uploadsDir = File(context.filesDir, "uploads")
            if (!uploadsDir.exists()) {
                uploadsDir.mkdirs()
            }

            var displayName = "فایل صوتی شخصی"
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1 && cursor.moveToFirst()) {
                    val name = cursor.getString(nameIndex)
                    if (!name.isNullOrBlank()) {
                        displayName = name
                    }
                }
            }

            // Sanitize file name extension
            val extension = displayName.substringAfterLast('.', "mp3")
            val baseName = displayName.substringBeforeLast('.')
            val targetFile = File(uploadsDir, "upload_${System.currentTimeMillis()}.$extension")

            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(targetFile).use { output ->
                    input.copyTo(output)
                }
            }

            var detectedTitle = baseName
            var detectedArtist = "فایل صوتی انتخابی کاربر"
            var durationSeconds = 30

            val retriever = MediaMetadataRetriever()
            try {
                retriever.setDataSource(targetFile.absolutePath)
                val metaTitle = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
                val metaArtist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
                val metaDuration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)

                if (!metaTitle.isNullOrBlank()) {
                    detectedTitle = metaTitle
                }
                if (!metaArtist.isNullOrBlank()) {
                    detectedArtist = metaArtist
                }
                val ms = metaDuration?.toLongOrNull() ?: 0L
                if (ms > 0) {
                    durationSeconds = (ms / 1000).toInt().coerceAtLeast(1)
                }
            } catch (e: Exception) {
                Log.w("AudioFileImporter", "Could not extract metadata", e)
            } finally {
                try {
                    retriever.release()
                } catch (_: Exception) {}
            }

            RowzehTrack(
                title = detectedTitle,
                speaker = detectedArtist,
                filePath = targetFile.absolutePath,
                isBuiltIn = false,
                isRecorded = false,
                isIncludedInRandom = true,
                durationSeconds = durationSeconds
            )
        } catch (e: Exception) {
            Log.e("AudioFileImporter", "Failed to import audio from uri: $uri", e)
            null
        }
    }
}
