package com.antigravity.livewallpaper.presentation.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.antigravity.livewallpaper.domain.model.AppThemeMode

private val MaterialDarkColorScheme = darkColorScheme(
    primary = PureDarkColors.primary,
    onPrimary = PureDarkColors.onPrimary,
    primaryContainer = PureDarkColors.surfaceVariant,
    onPrimaryContainer = PureDarkColors.primary,
    secondary = PureDarkColors.accent,
    onSecondary = PureDarkColors.onAccent,
    background = PureDarkColors.background,
    onBackground = PureDarkColors.textPrimary,
    surface = PureDarkColors.surface,
    onSurface = PureDarkColors.textPrimary,
    surfaceVariant = PureDarkColors.surfaceVariant,
    onSurfaceVariant = PureDarkColors.textSecondary,
    outline = PureDarkColors.border
)

private val MaterialLightColorScheme = lightColorScheme(
    primary = PureLightColors.primary,
    onPrimary = PureLightColors.onPrimary,
    primaryContainer = PureLightColors.surfaceVariant,
    onPrimaryContainer = PureLightColors.primary,
    secondary = PureLightColors.accent,
    onSecondary = PureLightColors.onAccent,
    background = PureLightColors.background,
    onBackground = PureLightColors.textPrimary,
    surface = PureLightColors.surface,
    onSurface = PureLightColors.textPrimary,
    surfaceVariant = PureLightColors.surfaceVariant,
    onSurfaceVariant = PureLightColors.textSecondary,
    outline = PureLightColors.border
)

@Composable
fun LiveWallpaperTheme(
    themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    accentIndex: Int = 0,
    content: @Composable () -> Unit
) {
    val systemInDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        AppThemeMode.DARK -> true
        AppThemeMode.LIGHT -> false
        AppThemeMode.SYSTEM -> systemInDark
    }

    val appColors = buildAppColors(isDark = isDark, accentIndex = accentIndex)
    val colorScheme = if (isDark) {
        darkColorScheme(
            primary = appColors.primary,
            onPrimary = appColors.onPrimary,
            primaryContainer = appColors.surfaceVariant,
            onPrimaryContainer = appColors.primary,
            secondary = appColors.accent,
            onSecondary = appColors.onAccent,
            background = appColors.background,
            onBackground = appColors.textPrimary,
            surface = appColors.surface,
            onSurface = appColors.textPrimary,
            surfaceVariant = appColors.surfaceVariant,
            onSurfaceVariant = appColors.textSecondary,
            outline = appColors.border
        )
    } else {
        lightColorScheme(
            primary = appColors.primary,
            onPrimary = appColors.onPrimary,
            primaryContainer = appColors.surfaceVariant,
            onPrimaryContainer = appColors.primary,
            secondary = appColors.accent,
            onSecondary = appColors.onAccent,
            background = appColors.background,
            onBackground = appColors.textPrimary,
            surface = appColors.surface,
            onSurface = appColors.textPrimary,
            surfaceVariant = appColors.surfaceVariant,
            onSurfaceVariant = appColors.textSecondary,
            outline = appColors.border
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            window.statusBarColor = appColors.background.toArgb()
            window.navigationBarColor = appColors.background.toArgb()
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = !isDark
            controller.isAppearanceLightNavigationBars = !isDark
        }
    }

    CompositionLocalProvider(LocalAppColors provides appColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
