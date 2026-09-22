package com.antigravity.livewallpaper.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class AppColors(
    val isDark: Boolean,
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val border: Color,
    val borderSubtle: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val primary: Color,
    val onPrimary: Color,
    val primaryVariant: Color,
    val accent: Color,
    val onAccent: Color,
    val badgeBackground: Color,
    val cardBackground: Color,
    val cardPressed: Color,
    val iconTint: Color
)

// Pure Dark Mode: 100% OLED pitch black background with glowing cyan/emerald accents
val PureDarkColors = AppColors(
    isDark = true,
    background = Color(0xFF000000),
    surface = Color(0xFF101216),
    surfaceVariant = Color(0xFF181C24),
    border = Color(0xFF263244),
    borderSubtle = Color(0xFF1B2330),
    textPrimary = Color(0xFFF8FAFC),
    textSecondary = Color(0xFF94A3B8),
    textMuted = Color(0xFF64748B),
    primary = Color(0xFF00E5FF),
    onPrimary = Color(0xFF000000),
    primaryVariant = Color(0xFF00B4D8),
    accent = Color(0xFF00F5A0),
    onAccent = Color(0xFF000000),
    badgeBackground = Color(0x2600E5FF),
    cardBackground = Color(0xFF101216),
    cardPressed = Color(0xFF181C24),
    iconTint = Color(0xFFF8FAFC)
)

// Pure Light Mode: 100% white background with tactile raised layers and deep teal primary (from UI materials)
val PureLightColors = AppColors(
    isDark = false,
    background = Color(0xFFFFFFFF),
    surface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFFEEF2F6),
    border = Color(0xFFE2E8F0),
    borderSubtle = Color(0xFFEDF2F7),
    textPrimary = Color(0xFF0F172A),
    textSecondary = Color(0xFF475569),
    textMuted = Color(0xFF94A3B8),
    primary = Color(0xFF0D5C68),
    onPrimary = Color(0xFFFFFFFF),
    primaryVariant = Color(0xFF0A4852),
    accent = Color(0xFF0D9488),
    onAccent = Color(0xFFFFFFFF),
    badgeBackground = Color(0x1A0D5C68),
    cardBackground = Color(0xFFF8FAFC),
    cardPressed = Color(0xFFE2E8F0),
    iconTint = Color(0xFF0F172A)
)

val LocalAppColors = staticCompositionLocalOf { PureDarkColors }

object AppTheme {
    val colors: AppColors
        @Composable
        @ReadOnlyComposable
        get() = LocalAppColors.current
}

data class AccentPalette(
    val name: String,
    val darkPrimary: Color,
    val darkVariant: Color,
    val lightPrimary: Color,
    val lightVariant: Color
)

val AppAccentPalettes = listOf(
    AccentPalette(
        name = "Cyber Cyan",
        darkPrimary = Color(0xFF00E5FF),
        darkVariant = Color(0xFF00B4D8),
        lightPrimary = Color(0xFF007A8A),
        lightVariant = Color(0xFF005E6A)
    ),
    AccentPalette(
        name = "Ocean Teal",
        darkPrimary = Color(0xFF0D9488),
        darkVariant = Color(0xFF0F766E),
        lightPrimary = Color(0xFF0D5C68),
        lightVariant = Color(0xFF0A4852)
    ),
    AccentPalette(
        name = "Vivid Violet",
        darkPrimary = Color(0xFF8B5CF6),
        darkVariant = Color(0xFF7C3AED),
        lightPrimary = Color(0xFF6D28D9),
        lightVariant = Color(0xFF5B21B6)
    ),
    AccentPalette(
        name = "Emerald Aurora",
        darkPrimary = Color(0xFF00F5A0),
        darkVariant = Color(0xFF10B981),
        lightPrimary = Color(0xFF059669),
        lightVariant = Color(0xFF047857)
    ),
    AccentPalette(
        name = "Sunset Amber",
        darkPrimary = Color(0xFFFFB800),
        darkVariant = Color(0xFFF59E0B),
        lightPrimary = Color(0xFFD97706),
        lightVariant = Color(0xFFB45309)
    ),
    AccentPalette(
        name = "Cyber Pink",
        darkPrimary = Color(0xFFFF007F),
        darkVariant = Color(0xFFEC4899),
        lightPrimary = Color(0xFFDB2777),
        lightVariant = Color(0xFFBE185D)
    )
)

fun buildAppColors(isDark: Boolean, accentIndex: Int = 0): AppColors {
    val palette = AppAccentPalettes.getOrElse(accentIndex) { AppAccentPalettes[0] }
    return if (isDark) {
        PureDarkColors.copy(
            primary = palette.darkPrimary,
            primaryVariant = palette.darkVariant,
            cardBackground = Color(0xFF101216),
            cardPressed = Color(0xFF181C24),
            badgeBackground = palette.darkPrimary.copy(alpha = 0.15f)
        )
    } else {
        PureLightColors.copy(
            primary = palette.lightPrimary,
            primaryVariant = palette.lightVariant,
            badgeBackground = palette.lightPrimary.copy(alpha = 0.12f)
        )
    }
}

// Accent & Legacy Palette Constants for backward compatibility
val NeonCyan = Color(0xFF00F0FF)
val NeonPurple = Color(0xFF8A2BE2)
val NeonPink = Color(0xFFFF007F)
val NeonAmber = Color(0xFFFFB800)
val NeonEmerald = Color(0xFF00F5A0)

val DarkBackground = Color(0xFF000000)
val DarkSurface = Color(0xFF101216)
val DarkSurfaceVariant = Color(0xFF181C24)
val DarkBorder = Color(0xFF263244)

val TextPrimary = Color(0xFFF8FAFC)
val TextSecondary = Color(0xFF94A3B8)
val TextMuted = Color(0xFF64748B)

val CardGlassBackground = Color(0xCC101216)
val AccentGlow = Color(0x3300F0FF)
