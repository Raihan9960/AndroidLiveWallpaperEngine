package com.antigravity.livewallpaper.presentation.navigation

sealed class Screen(val route: String) {
    data object Gallery : Screen("gallery")
    data object Preview : Screen("preview/{wallpaperId}") {
        fun createRoute(wallpaperId: String) = "preview/$wallpaperId"
    }
    data object Settings : Screen("settings")
    data object Onboarding : Screen("onboarding")
}
