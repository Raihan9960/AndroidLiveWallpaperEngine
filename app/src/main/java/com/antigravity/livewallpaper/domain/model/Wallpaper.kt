package com.antigravity.livewallpaper.domain.model

data class Wallpaper(
    val id: String,
    val title: String,
    val description: String = "",
    val type: WallpaperType,
    val sourceUri: String,
    val thumbnailUri: String,
    val isBuiltIn: Boolean = false,
    val resolution: String = "1080x1920",
    val durationMs: Long = 0L,
    val batteryImpact: BatteryImpact = BatteryImpact.LOW,
    val dateAdded: Long = System.currentTimeMillis()
)
