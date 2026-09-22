package com.antigravity.livewallpaper.domain.model

enum class BatteryImpact(
    val label: String,
    val grade: String,
    val description: String
) {
    ULTRA_LOW("Ultra Low", "A+", "Near zero draw; image suspended when off screen"),
    LOW("Optimized", "A", "Hardware decode loop capped at 30 FPS"),
    BALANCED("Balanced", "B+", "Standard 30-60 FPS dynamic playback"),
    PERFORMANCE("Maximum", "B", "Uncapped playback with full refresh rate")
}
