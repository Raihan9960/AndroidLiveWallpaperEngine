package com.antigravity.livewallpaper.data.local

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import com.antigravity.livewallpaper.domain.model.BatteryImpact
import com.antigravity.livewallpaper.domain.model.Wallpaper
import com.antigravity.livewallpaper.domain.model.WallpaperType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaStorageManager @Inject constructor(
    private val context: Context
) {
    companion object {
        private const val TAG = "MediaStorageManager"
        private const val WALLPAPERS_DIR = "custom_wallpapers"
        private const val THUMBNAILS_DIR = "thumbnails"
        private const val TRANSCODE_CACHE_DIR = "transcode_cache"
        private const val METADATA_FILE = "imported_wallpapers.json"
    }

    private val wallpapersDir = File(context.filesDir, WALLPAPERS_DIR).apply { mkdirs() }
    private val thumbnailsDir = File(context.filesDir, THUMBNAILS_DIR).apply { mkdirs() }
    val transcodeCacheDir = File(context.cacheDir, TRANSCODE_CACHE_DIR).apply { mkdirs() }
    private val metaFile = File(context.filesDir, METADATA_FILE)

    suspend fun getImportedWallpapers(): List<Wallpaper> = withContext(Dispatchers.IO) {
        if (!metaFile.exists()) return@withContext emptyList()
        try {
            val jsonStr = metaFile.readText()
            val array = JSONArray(jsonStr)
            val list = mutableListOf<Wallpaper>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    Wallpaper(
                        id = obj.getString("id"),
                        title = obj.getString("title"),
                        description = obj.optString("description", "Custom imported wallpaper"),
                        type = WallpaperType.valueOf(obj.getString("type")),
                        sourceUri = obj.getString("sourceUri"),
                        thumbnailUri = obj.getString("thumbnailUri"),
                        isBuiltIn = false,
                        resolution = obj.optString("resolution", "1080x1920"),
                        durationMs = obj.optLong("durationMs", 0L),
                        batteryImpact = try {
                            BatteryImpact.valueOf(obj.optString("batteryImpact", BatteryImpact.LOW.name))
                        } catch (e: Exception) {
                            BatteryImpact.LOW
                        },
                        dateAdded = obj.optLong("dateAdded", System.currentTimeMillis())
                    )
                )
            }
            list
        } catch (e: Exception) {
            Log.e(TAG, "Error reading wallpaper metadata", e)
            emptyList()
        }
    }

    private suspend fun saveImportedWallpapers(list: List<Wallpaper>) = withContext(Dispatchers.IO) {
        try {
            val array = JSONArray()
            for (w in list) {
                val obj = JSONObject().apply {
                    put("id", w.id)
                    put("title", w.title)
                    put("description", w.description)
                    put("type", w.type.name)
                    put("sourceUri", w.sourceUri)
                    put("thumbnailUri", w.thumbnailUri)
                    put("resolution", w.resolution)
                    put("durationMs", w.durationMs)
                    put("batteryImpact", w.batteryImpact.name)
                    put("dateAdded", w.dateAdded)
                }
                array.put(obj)
            }
            metaFile.writeText(array.toString())
        } catch (e: Exception) {
            Log.e(TAG, "Error saving wallpaper metadata", e)
        }
    }

    suspend fun importMedia(uri: Uri, userGivenTitle: String? = null): Result<Wallpaper> = withContext(Dispatchers.IO) {
        try {
            val mimeType = context.contentResolver.getType(uri) ?: ""
            val id = UUID.randomUUID().toString()

            val type = when {
                mimeType.contains("gif") -> WallpaperType.GIF
                mimeType.contains("video") || mimeType.contains("mp4") || mimeType.contains("mkv") || mimeType.contains("webm") -> WallpaperType.VIDEO
                else -> WallpaperType.IMAGE
            }

            val extension = when (type) {
                WallpaperType.VIDEO -> "mp4"
                WallpaperType.GIF -> "gif"
                WallpaperType.IMAGE -> "jpg"
            }

            val destFile = File(wallpapersDir, "wp_$id.$extension")
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(destFile).use { output ->
                    input.copyTo(output)
                }
            } ?: return@withContext Result.failure(Exception("Cannot open content stream"))

            // Create thumbnail and read metadata
            val thumbFile = File(thumbnailsDir, "thumb_$id.jpg")
            var resolution = "1080x1920"
            var durationMs = 0L

            if (type == WallpaperType.VIDEO) {
                val retriever = MediaMetadataRetriever()
                try {
                    retriever.setDataSource(destFile.absolutePath)
                    val width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH) ?: "1080"
                    val height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT) ?: "1920"
                    resolution = "${width}x${height}"
                    durationMs = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0L

                    val frame = retriever.getFrameAtTime(1000000) // 1 second in
                    if (frame != null) {
                        FileOutputStream(thumbFile).use { out ->
                            frame.compress(Bitmap.CompressFormat.JPEG, 85, out)
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error extracting video metadata", e)
                } finally {
                    retriever.release()
                }
            } else {
                // Static image or GIF thumbnail
                val bmp = BitmapFactory.decodeFile(destFile.absolutePath)
                if (bmp != null) {
                    resolution = "${bmp.width}x${bmp.height}"
                    FileOutputStream(thumbFile).use { out ->
                        bmp.compress(Bitmap.CompressFormat.JPEG, 85, out)
                    }
                }
            }

            val resolvedTitle = userGivenTitle
                ?: getDisplayNameFromUri(uri)
                ?: "Imported $type"
            val wallpaper = Wallpaper(
                id = id,
                title = resolvedTitle,
                description = "Imported $type wallpaper ($resolution)",
                type = type,
                sourceUri = "file://${destFile.absolutePath}",
                thumbnailUri = if (thumbFile.exists()) "file://${thumbFile.absolutePath}" else "file://${destFile.absolutePath}",
                isBuiltIn = false,
                resolution = resolution,
                durationMs = durationMs,
                batteryImpact = when (type) {
                    WallpaperType.IMAGE -> BatteryImpact.ULTRA_LOW
                    WallpaperType.GIF -> BatteryImpact.BALANCED
                    WallpaperType.VIDEO -> BatteryImpact.LOW
                }
            )

            val current = getImportedWallpapers().toMutableList()
            current.add(0, wallpaper)
            saveImportedWallpapers(current)

            Result.success(wallpaper)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to import media", e)
            Result.failure(e)
        }
    }

    suspend fun deleteWallpaper(id: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val current = getImportedWallpapers().toMutableList()
            val target = current.find { it.id == id } ?: return@withContext false

            current.removeAll { it.id == id }
            saveImportedWallpapers(current)

            // Remove files
            if (target.sourceUri.startsWith("file://")) {
                File(Uri.parse(target.sourceUri).path ?: "").delete()
            }
            if (target.thumbnailUri.startsWith("file://")) {
                File(Uri.parse(target.thumbnailUri).path ?: "").delete()
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting wallpaper", e)
            false
        }
    }

    suspend fun getTranscodeCacheSizeBytes(): Long = withContext(Dispatchers.IO) {
        var size = 0L
        transcodeCacheDir.walkTopDown().forEach { file ->
            if (file.isFile) size += file.length()
        }
        size
    }

    suspend fun clearTranscodeCache(): Boolean = withContext(Dispatchers.IO) {
        try {
            transcodeCacheDir.listFiles()?.forEach { it.delete() }
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun getDisplayNameFromUri(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            try {
                context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        if (index != -1) {
                            result = cursor.getString(index)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "Failed to resolve display name from uri", e)
            }
        }
        if (result == null) {
            result = uri.lastPathSegment
        }
        return result?.substringBeforeLast('.')?.takeIf { it.isNotBlank() }
    }
}
