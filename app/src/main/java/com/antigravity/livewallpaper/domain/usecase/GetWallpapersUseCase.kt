package com.antigravity.livewallpaper.domain.usecase

import com.antigravity.livewallpaper.domain.model.Wallpaper
import com.antigravity.livewallpaper.domain.repository.WallpaperRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetWallpapersUseCase @Inject constructor(
    private val repository: WallpaperRepository
) {
    fun getBuiltIn(): Flow<List<Wallpaper>> = repository.getBuiltInWallpapers()
    fun getMyWallpapers(): Flow<List<Wallpaper>> = repository.getMyWallpapers()
    fun getActiveWallpaper(): Flow<Wallpaper?> = repository.getActiveWallpaper()
}
