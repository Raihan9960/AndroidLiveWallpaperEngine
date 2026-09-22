package com.antigravity.livewallpaper.presentation.gallery

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antigravity.livewallpaper.domain.model.Wallpaper
import com.antigravity.livewallpaper.domain.repository.WallpaperRepository
import com.antigravity.livewallpaper.domain.usecase.GetWallpapersUseCase
import com.antigravity.livewallpaper.domain.usecase.ImportMediaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ImportUiState {
    data object Idle : ImportUiState
    data object Importing : ImportUiState
    data class Success(val wallpaper: Wallpaper) : ImportUiState
    data class Error(val message: String) : ImportUiState
}

@HiltViewModel
class GalleryViewModel @Inject constructor(
    getWallpapersUseCase: GetWallpapersUseCase,
    private val importMediaUseCase: ImportMediaUseCase,
    private val repository: WallpaperRepository
) : ViewModel() {

    val curatedWallpapers: StateFlow<List<Wallpaper>> = getWallpapersUseCase.getBuiltIn()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val myWallpapers: StateFlow<List<Wallpaper>> = getWallpapersUseCase.getMyWallpapers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeWallpaper: StateFlow<Wallpaper?> = getWallpapersUseCase.getActiveWallpaper()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _importState = MutableStateFlow<ImportUiState>(ImportUiState.Idle)
    val importState: StateFlow<ImportUiState> = _importState.asStateFlow()

    fun importMedia(uri: Uri) {
        viewModelScope.launch {
            _importState.value = ImportUiState.Importing
            val result = importMediaUseCase(uri)
            if (result.isSuccess) {
                _importState.value = ImportUiState.Success(result.getOrThrow())
            } else {
                _importState.value = ImportUiState.Error(
                    result.exceptionOrNull()?.message ?: "Failed to import media"
                )
            }
        }
    }

    fun resetImportState() {
        _importState.value = ImportUiState.Idle
    }

    fun deleteCustomWallpaper(id: String) {
        viewModelScope.launch {
            repository.deleteCustomWallpaper(id)
        }
    }
}
