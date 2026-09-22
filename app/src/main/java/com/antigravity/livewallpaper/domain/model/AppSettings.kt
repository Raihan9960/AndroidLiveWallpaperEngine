package com.antigravity.livewallpaper.domain.model

data class AppSettings(
    val frameRateCap: Int = 30,
    val batterySaverAdaptive: Boolean = true,
    val parallaxEnabled: Boolean = true,
    val transcodeCeiling1080p: Boolean = true,
    val hasCompletedOnboarding: Boolean = false,
    val activeWallpaperId: String? = null
)
