package com.antigravity.livewallpaper.data.repository

import android.net.Uri
import com.antigravity.livewallpaper.data.local.BuiltInCatalog
import com.antigravity.livewallpaper.data.local.MediaStorageManager
import com.antigravity.livewallpaper.data.local.WallpaperPreferences
import com.antigravity.livewallpaper.domain.model.AppSettings
import com.antigravity.livewallpaper.domain.model.Wallpaper
import com.antigravity.livewallpaper.domain.repository.WallpaperRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WallpaperRepositoryImpl @Inject constructor(
    private val preferences: WallpaperPreferences,
    private val mediaStorageManager: MediaStorageManager
) : WallpaperRepository {

    private val repositoryScope = CoroutineScope(Dispatchers.IO)
    private val myWallpapersState = MutableStateFlow<List<Wallpaper>>(emptyList())

    init {
        refreshMyWallpapers()
    }

    private fun refreshMyWallpapers() {
        repositoryScope.launch {
            try {
                val list = mediaStorageManager.getImportedWallpapers()
                myWallpapersState.value = list
            } catch (e: Exception) {
                // Ignore initialization read errors
            }
        }
    }

    override fun getBuiltInWallpapers(): Flow<List<Wallpaper>> = flow {
        emit(BuiltInCatalog.getCatalog())
    }

    override fun getMyWallpapers(): Flow<List<Wallpaper>> = myWallpapersState.asStateFlow()

    override fun getActiveWallpaper(): Flow<Wallpaper?> {
        return preferences.activeWallpaperFlow.map { saved ->
            saved ?: myWallpapersState.value.firstOrNull()
        }
    }

    override fun getSettings(): Flow<AppSettings> = preferences.settingsFlow

    override suspend fun setActiveWallpaper(wallpaper: Wallpaper) {
        preferences.setActiveWallpaper(wallpaper)
    }

    override suspend fun importMedia(uri: Uri, title: String?): Result<Wallpaper> {
        val result = mediaStorageManager.importMedia(uri, title)
        if (result.isSuccess) {
            myWallpapersState.value = mediaStorageManager.getImportedWallpapers()
        }
        return result
    }

    override suspend fun deleteCustomWallpaper(id: String): Boolean {
        val deleted = mediaStorageManager.deleteWallpaper(id)
        if (deleted) {
            myWallpapersState.value = mediaStorageManager.getImportedWallpapers()
        }
        return deleted
    }

    override suspend fun updateFrameRateCap(fps: Int) {
        preferences.updateFrameRateCap(fps)
    }

    override suspend fun updateBatterySaverAdaptive(enabled: Boolean) {
        preferences.updateBatterySaverAdaptive(enabled)
    }

    override suspend fun updateParallaxEnabled(enabled: Boolean) {
        preferences.updateParallaxEnabled(enabled)
    }

    override suspend fun updateThemeMode(mode: com.antigravity.livewallpaper.domain.model.AppThemeMode) {
        preferences.updateThemeMode(mode)
    }

    override suspend fun updateAccentColorIndex(index: Int) {
        preferences.updateAccentColorIndex(index)
    }

    override suspend fun updateVideoScalingMode(mode: Int) {
        preferences.updateVideoScalingMode(mode)
    }

    override suspend fun updateAudioEnabled(enabled: Boolean) {
        preferences.updateAudioEnabled(enabled)
    }

    override suspend fun updateLowBatteryCutoff(enabled: Boolean) {
        preferences.updateLowBatteryCutoff(enabled)
    }

    override suspend fun updateParallaxSensitivity(sensitivity: Float) {
        preferences.updateParallaxSensitivity(sensitivity)
    }

    override suspend fun updateDoubleTapAction(enabled: Boolean) {
        preferences.updateDoubleTapAction(enabled)
    }

    override suspend fun updateWallpaperScope(scope: com.antigravity.livewallpaper.domain.model.WallpaperScope) {
        preferences.updateWallpaperScope(scope)
    }

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        preferences.setOnboardingCompleted(completed)
    }

    override suspend fun getCacheSizeBytes(): Long {
        return mediaStorageManager.getTranscodeCacheSizeBytes()
    }

    override suspend fun clearCache(): Boolean {
        return mediaStorageManager.clearTranscodeCache()
    }
}
