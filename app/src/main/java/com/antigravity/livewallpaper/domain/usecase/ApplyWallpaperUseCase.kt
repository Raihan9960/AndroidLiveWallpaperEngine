package com.antigravity.livewallpaper.domain.usecase

import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.antigravity.livewallpaper.domain.model.Wallpaper
import com.antigravity.livewallpaper.domain.repository.WallpaperRepository
import com.antigravity.livewallpaper.service.LiveWallpaperService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ApplyWallpaperUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: WallpaperRepository
) {
    suspend operator fun invoke(wallpaper: Wallpaper): Result<Unit> {
        return try {
            // Save active selection to DataStore
            repository.setActiveWallpaper(wallpaper)

            // Trigger system live wallpaper apply intent with component name
            val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).apply {
                putExtra(
                    WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                    ComponentName(context, LiveWallpaperService::class.java)
                )
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
