package com.antigravity.livewallpaper.presentation.settings

import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antigravity.livewallpaper.domain.model.AppSettings
import com.antigravity.livewallpaper.domain.model.AppThemeMode
import com.antigravity.livewallpaper.domain.model.Wallpaper
import com.antigravity.livewallpaper.domain.repository.WallpaperRepository
import com.antigravity.livewallpaper.service.LiveWallpaperService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: WallpaperRepository
) : ViewModel() {

    val settings: StateFlow<AppSettings> = repository.getSettings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppSettings())

    val activeWallpaper: StateFlow<Wallpaper?> = repository.getActiveWallpaper()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _cacheSize = MutableStateFlow("0 MB")
    val cacheSize: StateFlow<String> = _cacheSize.asStateFlow()

    init {
        refreshCacheSize()
    }

    fun refreshCacheSize() {
        viewModelScope.launch {
            val bytes = repository.getCacheSizeBytes()
            val mb = bytes.toDouble() / (1024 * 1024)
            _cacheSize.value = "${DecimalFormat("#0.1").format(mb)} MB"
        }
    }

    fun setFrameRateCap(fps: Int) {
        viewModelScope.launch {
            repository.updateFrameRateCap(fps)
        }
    }

    fun setBatterySaverAdaptive(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateBatterySaverAdaptive(enabled)
        }
    }

    fun setParallaxEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateParallaxEnabled(enabled)
        }
    }

    fun setThemeMode(mode: AppThemeMode) {
        viewModelScope.launch {
            repository.updateThemeMode(mode)
        }
    }

    fun setAccentColorIndex(index: Int) {
        viewModelScope.launch {
            repository.updateAccentColorIndex(index)
        }
    }

    fun setVideoScalingMode(mode: Int) {
        viewModelScope.launch {
            repository.updateVideoScalingMode(mode)
        }
    }

    fun setAudioEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateAudioEnabled(enabled)
        }
    }

    fun setLowBatteryCutoff(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateLowBatteryCutoff(enabled)
        }
    }

    fun setParallaxSensitivity(sensitivity: Float) {
        viewModelScope.launch {
            repository.updateParallaxSensitivity(sensitivity)
        }
    }

    fun setDoubleTapAction(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateDoubleTapAction(enabled)
        }
    }

    fun clearCache() {
        viewModelScope.launch {
            repository.clearCache()
            refreshCacheSize()
        }
    }

    fun openSystemWallpaperChooser(context: Context) {
        try {
            val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).apply {
                putExtra(
                    WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                    ComponentName(context, LiveWallpaperService::class.java)
                )
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            try {
                val chooserIntent = Intent(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(chooserIntent)
            } catch (err: Exception) {
                Toast.makeText(context, "Could not open system wallpaper picker", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
