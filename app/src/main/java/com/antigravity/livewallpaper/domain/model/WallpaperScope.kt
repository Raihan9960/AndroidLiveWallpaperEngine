package com.antigravity.livewallpaper.domain.model

/**
 * Controls which screen surface(s) the live wallpaper should be applied to.
 *
 * Note: Actual surface assignment is always done via the Android system wallpaper picker.
 * This preference stores the user's *intent* and drives the picker launch.
 *
 * - HOME_ONLY  → standard home-screen-only (default)
 * - BOTH       → home screen + lock screen simultaneously
 * - LOCK_ONLY  → lock screen only (where OEM supports it)
 */
enum class WallpaperScope {
    HOME_ONLY,
    BOTH,
    LOCK_ONLY
}
