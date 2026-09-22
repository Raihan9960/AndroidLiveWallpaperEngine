package com.antigravity.livewallpaper.data.media

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.transformer.Composition
import androidx.media3.transformer.EditedMediaItem
import androidx.media3.transformer.EditedMediaItemSequence
import androidx.media3.transformer.ExportException
import androidx.media3.transformer.ExportResult
import androidx.media3.transformer.ProgressHolder
import androidx.media3.transformer.TransformationRequest
import androidx.media3.transformer.Transformer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

sealed interface TranscodeState {
    data object Idle : TranscodeState
    data class Progress(val percentage: Int) : TranscodeState
    data class Success(val outputFile: File) : TranscodeState
    data class Failure(val error: String) : TranscodeState
}

@Singleton
class VideoTranscoder @Inject constructor(
    private val context: Context
) {
    companion object {
        private const val TAG = "VideoTranscoder"
    }

    suspend fun transcodeVideo(inputUri: Uri): Flow<TranscodeState> = callbackFlow {
        val outputDir = File(context.cacheDir, "transcode_cache").apply { mkdirs() }
        val outputFile = File(outputDir, "transcoded_${UUID.randomUUID()}.mp4")

        val transformer = Transformer.Builder(context)
            .addListener(object : Transformer.Listener {
                override fun onCompleted(composition: Composition, exportResult: ExportResult) {
                    trySend(TranscodeState.Success(outputFile))
                    close()
                }

                override fun onError(
                    composition: Composition,
                    exportResult: ExportResult,
                    exportException: ExportException
                ) {
                    Log.e(TAG, "Transcode error", exportException)
                    trySend(TranscodeState.Failure(exportException.message ?: "Unknown transcode error"))
                    close()
                }
            })
            .build()

        val mediaItem = MediaItem.fromUri(inputUri)
        val editedMediaItem = EditedMediaItem.Builder(mediaItem).build()

        try {
            trySend(TranscodeState.Progress(0))
            transformer.start(editedMediaItem, outputFile.absolutePath)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start transformer", e)
            trySend(TranscodeState.Failure(e.message ?: "Failed to start video transcoding"))
            close()
        }

        awaitClose {
            try {
                transformer.cancel()
            } catch (e: Exception) {
                // Ignore cancel errors
            }
        }
    }
}
