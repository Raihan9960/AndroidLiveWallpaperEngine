package com.antigravity.livewallpaper.presentation.preview

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antigravity.livewallpaper.domain.model.Wallpaper
import com.antigravity.livewallpaper.domain.repository.WallpaperRepository
import com.antigravity.livewallpaper.domain.usecase.ApplyWallpaperUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ApplyUiState {
    data object Idle : ApplyUiState
    data object Applying : ApplyUiState
    data object Success : ApplyUiState
    data class Error(val message: String) : ApplyUiState
}

@HiltViewModel
class PreviewViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: WallpaperRepository,
    private val applyWallpaperUseCase: ApplyWallpaperUseCase
) : ViewModel() {

    private val wallpaperId: String = checkNotNull(savedStateHandle["wallpaperId"])

    private val _wallpaper = MutableStateFlow<Wallpaper?>(null)
    val wallpaper: StateFlow<Wallpaper?> = _wallpaper.asStateFlow()

    private val _applyState = MutableStateFlow<ApplyUiState>(ApplyUiState.Idle)
    val applyState: StateFlow<ApplyUiState> = _applyState.asStateFlow()

    private val _simulateIcons = MutableStateFlow(false)
    val simulateIcons: StateFlow<Boolean> = _simulateIcons.asStateFlow()

    init {
        loadWallpaper()
    }

    private fun loadWallpaper() {
        viewModelScope.launch {
            val builtIn = repository.getBuiltInWallpapers().firstOrNull() ?: emptyList()
            val my = repository.getMyWallpapers().firstOrNull() ?: emptyList()
            val all = builtIn + my
            _wallpaper.value = all.find { it.id == wallpaperId }
        }
    }

    fun toggleSimulateIcons() {
        _simulateIcons.value = !_simulateIcons.value
    }

    fun applyWallpaper() {
        val wp = _wallpaper.value ?: return
        viewModelScope.launch {
            _applyState.value = ApplyUiState.Applying
            val result = applyWallpaperUseCase(wp)
            if (result.isSuccess) {
                _applyState.value = ApplyUiState.Success
            } else {
                _applyState.value = ApplyUiState.Error(
                    result.exceptionOrNull()?.message ?: "Failed to set wallpaper"
                )
            }
        }
    }

    fun resetApplyState() {
        _applyState.value = ApplyUiState.Idle
    }
}
