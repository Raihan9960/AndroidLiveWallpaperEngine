package com.antigravity.livewallpaper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import com.antigravity.livewallpaper.domain.model.AppSettings
import com.antigravity.livewallpaper.domain.repository.WallpaperRepository
import com.antigravity.livewallpaper.presentation.navigation.LiveWallpaperNavHost
import com.antigravity.livewallpaper.presentation.theme.LiveWallpaperTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var repository: WallpaperRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settings by repository.getSettings().collectAsState(initial = AppSettings())
            LiveWallpaperTheme(
                themeMode = settings.themeMode,
                accentIndex = settings.accentColorIndex
            ) {
                val navController = rememberNavController()
                LiveWallpaperNavHost(navController = navController)
            }
        }
    }
}
