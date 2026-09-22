package com.antigravity.livewallpaper.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antigravity.livewallpaper.domain.model.AppSettings
import com.antigravity.livewallpaper.domain.repository.WallpaperRepository
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

    fun clearCache() {
        viewModelScope.launch {
            repository.clearCache()
            refreshCacheSize()
        }
    }
}
