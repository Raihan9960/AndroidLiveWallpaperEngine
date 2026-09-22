package com.antigravity.livewallpaper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.antigravity.livewallpaper.presentation.navigation.LiveWallpaperNavHost
import com.antigravity.livewallpaper.presentation.theme.LiveWallpaperTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LiveWallpaperTheme {
                val navController = rememberNavController()
                LiveWallpaperNavHost(navController = navController)
            }
        }
    }
}
