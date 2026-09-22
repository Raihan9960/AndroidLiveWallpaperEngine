package com.antigravity.livewallpaper.domain.model

data class AppSettings(
    val frameRateCap: Int = 30,
    val batterySaverAdaptive: Boolean = true,
    val parallaxEnabled: Boolean = true,
    val transcodeCeiling1080p: Boolean = true,
    val themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    val hasCompletedOnboarding: Boolean = false,
    val activeWallpaperId: String? = null,
    val accentColorIndex: Int = 0,
    val videoScalingMode: Int = 0,
    val audioEnabled: Boolean = false,
    val lowBatteryCutoff: Boolean = true,
    val parallaxSensitivity: Float = 1.0f,
    val doubleTapAction: Boolean = false,
    val wallpaperScope: WallpaperScope = WallpaperScope.HOME_ONLY
)
