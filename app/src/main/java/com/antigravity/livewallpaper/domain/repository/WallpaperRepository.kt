package com.antigravity.livewallpaper.domain.repository

import android.net.Uri
import com.antigravity.livewallpaper.domain.model.AppSettings
import com.antigravity.livewallpaper.domain.model.Wallpaper
import kotlinx.coroutines.flow.Flow

interface WallpaperRepository {
    fun getBuiltInWallpapers(): Flow<List<Wallpaper>>
    fun getMyWallpapers(): Flow<List<Wallpaper>>
    fun getActiveWallpaper(): Flow<Wallpaper?>
    fun getSettings(): Flow<AppSettings>
    suspend fun setActiveWallpaper(wallpaper: Wallpaper)
    suspend fun importMedia(uri: Uri, title: String? = null): Result<Wallpaper>
    suspend fun deleteCustomWallpaper(id: String): Boolean
    suspend fun updateFrameRateCap(fps: Int)
    suspend fun updateBatterySaverAdaptive(enabled: Boolean)
    suspend fun updateParallaxEnabled(enabled: Boolean)
    suspend fun updateThemeMode(mode: com.antigravity.livewallpaper.domain.model.AppThemeMode)
    suspend fun updateAccentColorIndex(index: Int)
    suspend fun updateVideoScalingMode(mode: Int)
    suspend fun updateAudioEnabled(enabled: Boolean)
    suspend fun updateLowBatteryCutoff(enabled: Boolean)
    suspend fun updateParallaxSensitivity(sensitivity: Float)
    suspend fun updateDoubleTapAction(enabled: Boolean)
    suspend fun updateWallpaperScope(scope: com.antigravity.livewallpaper.domain.model.WallpaperScope)
    suspend fun setOnboardingCompleted(completed: Boolean)
    suspend fun getCacheSizeBytes(): Long
    suspend fun clearCache(): Boolean
}
