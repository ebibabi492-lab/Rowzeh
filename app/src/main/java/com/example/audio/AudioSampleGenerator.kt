package com.example.audio

import android.content.Context
import com.example.data.model.RowzehTrack
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

object AudioSampleGenerator {

    fun generateDefaultSamplesIfEmpty(context: Context): List<RowzehTrack> {
        val samplesDir = File(context.filesDir, "samples")
        if (!samplesDir.exists()) {
            samplesDir.mkdirs()
        }

        val samples = listOf(
            SampleDef(
                fileName = "rowzeh_seyedoshohada.wav",
                title = "روضه و مرثیه سیدالشهداء (ع)",
                speaker = "نوای سنتی عاشورایی",
                baseFrequencies = doubleArrayOf(220.0, 261.63, 293.66, 329.63, 392.0),
                durationSec = 16
            ),
            SampleDef(
                fileName = "zekr_salawat.wav",
                title = "ذکر و صلوات معنوی اهل بیت (ع)",
                speaker = "آوای معنوی ذاکرین",
                baseFrequencies = doubleArrayOf(196.0, 246.94, 293.66, 369.99),
                durationSec = 14
            ),
            SampleDef(
                fileName = "marseye_dashti.wav",
                title = "مرثیه و نی‌نوازی در مایه دشتی",
                speaker = "نوای دلنشین نی و سوز روضه",
                baseFrequencies = doubleArrayOf(220.0, 246.94, 261.63, 293.66, 329.63),
                durationSec = 18
            )
        )

        return samples.map { def ->
            val file = File(samplesDir, def.fileName)
            if (!file.exists() || file.length() < 1000) {
                createPersianSpiritualWav(file, def.baseFrequencies, def.durationSec)
            }
            RowzehTrack(
                title = def.title,
                speaker = def.speaker,
                filePath = file.absolutePath,
                isBuiltIn = true,
                isRecorded = false,
                isIncludedInRandom = true,
                durationSeconds = def.durationSec
            )
        }
    }

    private data class SampleDef(
        val fileName: String,
        val title: String,
        val speaker: String,
        val baseFrequencies: DoubleArray,
        val durationSec: Int
    )

    private fun createPersianSpiritualWav(file: File, notes: DoubleArray, durationSeconds: Int) {
        val sampleRate = 22050
        val totalSamples = sampleRate * durationSeconds
        val bytesPerSample = 2 // 16-bit
        val dataSize = totalSamples * bytesPerSample

        val buffer = ByteBuffer.allocate(44 + dataSize)
        buffer.order(ByteOrder.LITTLE_ENDIAN)

        // RIFF Header
        buffer.put("RIFF".toByteArray())
        buffer.putInt(36 + dataSize)
        buffer.put("WAVE".toByteArray())

        // "fmt " Subchunk
        buffer.put("fmt ".toByteArray())
        buffer.putInt(16) // Subchunk1Size for PCM
        buffer.putShort(1) // AudioFormat 1 = PCM
        buffer.putShort(1) // NumChannels = 1 (Mono)
        buffer.putInt(sampleRate) // SampleRate
        buffer.putInt(sampleRate * bytesPerSample) // ByteRate
        buffer.putShort(bytesPerSample.toShort()) // BlockAlign
        buffer.putShort(16) // BitsPerSample

        // "data" Subchunk
        buffer.put("data".toByteArray())
        buffer.putInt(dataSize)

        // Synthesize a soothing, spiritual Persian ney/reed ambient tone with gentle bell harmonics
        val noteCount = notes.size
        val secondsPerNote = durationSeconds.toDouble() / noteCount

        for (i in 0 until totalSamples) {
            val t = i.toDouble() / sampleRate
            val noteIndex = (t / secondsPerNote).toInt().coerceIn(0, noteCount - 1)
            val freq = notes[noteIndex]
            val noteLocalT = t % secondsPerNote

            // Envelope: soft swell and gentle decay
            val attack = (noteLocalT / 0.4).coerceAtMost(1.0)
            val release = exp(-noteLocalT / 2.5)
            val envelope = attack * release

            // Vibrato (tremolo modulation typical of traditional Ney)
            val vibrato = 1.0 + 0.015 * sin(2.0 * PI * 5.0 * t)

            // Harmonics
            val wave1 = sin(2.0 * PI * freq * vibrato * t)
            val wave2 = 0.4 * sin(4.0 * PI * freq * vibrato * t)
            val wave3 = 0.15 * sin(6.0 * PI * freq * vibrato * t)
            // Bell resonance
            val chime = 0.1 * sin(2.0 * PI * 880.0 * t) * exp(-noteLocalT * 2.0)

            val sampleVal = (wave1 + wave2 + wave3 + chime) * envelope * 0.6
            val clamped = (sampleVal * 32767.0).toInt().coerceIn(-32768, 32767)
            buffer.putShort(clamped.toShort())
        }

        FileOutputStream(file).use { fos ->
            fos.write(buffer.array())
        }
    }
}
