package com.antigravity.livewallpaper.presentation.settings

import android.widget.Toast
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BatterySaver
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.antigravity.livewallpaper.domain.model.AppThemeMode
import com.antigravity.livewallpaper.presentation.theme.AppAccentPalettes
import com.antigravity.livewallpaper.presentation.theme.AppTheme

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val settings by viewModel.settings.collectAsState()
    val activeWallpaper by viewModel.activeWallpaper.collectAsState()
    val cacheSize by viewModel.cacheSize.collectAsState()
    val colors = AppTheme.colors

    Scaffold(containerColor = colors.background) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // One UI Large-Title Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(colors.surface)
                        .border(1.dp, colors.border, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = colors.textPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.headlineLarge,
                    color = colors.textPrimary,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 30.sp
                )
                Text(
                    text = "Appearance, engine & device care",
                    color = colors.textMuted,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // ─────────────────────────────────────────────────────────────
                // OriginOS Live Status Capsule
                // ─────────────────────────────────────────────────────────────
                OriginOsStatusCapsule(
                    wallpaperTitle = activeWallpaper?.title ?: "No Live Wallpaper Set",
                    wallpaperUri = activeWallpaper?.thumbnailUri ?: activeWallpaper?.sourceUri,
                    frameRate = settings.frameRateCap,
                    onOpenChooser = { viewModel.openSystemWallpaperChooser(context) }
                )

                // ─────────────────────────────────────────────────────────────
                // SECTION 1 — APPEARANCE
                // ─────────────────────────────────────────────────────────────
                SectionCard(title = "APPEARANCE") {
                    // App Theme mode selector
                    RowHeader(
                        icon = Icons.Default.Palette,
                        gradient = listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8)),
                        title = "App Theme",
                        subtitle = "Light, dark or follow system"
                    )
                    ThreePillSelector(
                        items = listOf("System", "Light", "Dark"),
                        icons = listOf(Icons.Default.BrightnessAuto, Icons.Default.LightMode, Icons.Default.DarkMode),
                        selectedIndex = when (settings.themeMode) {
                            AppThemeMode.SYSTEM -> 0
                            AppThemeMode.LIGHT -> 1
                            AppThemeMode.DARK -> 2
                        },
                        onSelect = {
                            viewModel.setThemeMode(
                                when (it) {
                                    0 -> AppThemeMode.SYSTEM
                                    1 -> AppThemeMode.LIGHT
                                    else -> AppThemeMode.DARK
                                }
                            )
                        },
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )

                    RowDivider()

                    // Accent color palette
                    RowHeader(
                        icon = Icons.Default.BrightnessAuto,
                        gradient = listOf(Color(0xFF8B5CF6), Color(0xFFEC4899)),
                        title = "Accent Color",
                        subtitle = "System-wide signature color"
                    )
                    AccentPicker(
                        selectedIndex = settings.accentColorIndex,
                        onSelect = { viewModel.setAccentColorIndex(it) },
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }

                // ─────────────────────────────────────────────────────────────
                // SECTION 2 — PLAYBACK ENGINE
                // ─────────────────────────────────────────────────────────────
                SectionCard(title = "PLAYBACK ENGINE") {
                    // Frame Rate Cap
                    RowHeader(
                        icon = Icons.Default.Speed,
                        gradient = listOf(Color(0xFF10B981), Color(0xFF047857)),
                        title = "Frame Rate Cap",
                        subtitle = "Maximum rendering frequency"
                    )
                    ThreePillSelector(
                        items = listOf("24 FPS", "30 FPS", "60 FPS"),
                        subtitles = listOf("Eco", "Normal", "Ultra"),
                        selectedIndex = when (settings.frameRateCap) {
                            24 -> 0; 30 -> 1; else -> 2
                        },
                        onSelect = { viewModel.setFrameRateCap(listOf(24, 30, 60)[it]) },
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )

                    RowDivider()

                    // Battery saver adaptive
                    SwitchRow(
                        icon = Icons.Default.BatterySaver,
                        gradient = listOf(Color(0xFF10B981), Color(0xFF065F46)),
                        title = "Adaptive Battery Saver",
                        subtitle = "Slows video decoding when Battery Saver is active",
                        checked = settings.batterySaverAdaptive,
                        onCheckedChange = { viewModel.setBatterySaverAdaptive(it) }
                    )
                }

                // ─────────────────────────────────────────────────────────────
                // SECTION 3 — MOTION & PARALLAX
                // ─────────────────────────────────────────────────────────────
                SectionCard(title = "MOTION") {
                    SwitchRow(
                        icon = Icons.Default.ScreenRotation,
                        gradient = listOf(Color(0xFF06B6D4), Color(0xFF0E7490)),
                        title = "Home Screen Parallax",
                        subtitle = "Wallpaper pans as you swipe between launcher pages",
                        checked = settings.parallaxEnabled,
                        onCheckedChange = { viewModel.setParallaxEnabled(it) }
                    )
                }

                // ─────────────────────────────────────────────────────────────
                // SECTION 4 — DEVICE CARE
                // ─────────────────────────────────────────────────────────────
                SectionCard(title = "DEVICE CARE") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(13.dp))
                                .background(Brush.linearGradient(listOf(Color(0xFF0EA5E9), Color(0xFF0284C7)))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CleaningServices,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Cache Storage",
                                color = colors.textPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = cacheSize,
                                color = colors.textMuted,
                                fontSize = 13.sp
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFFEF4444).copy(alpha = 0.1f))
                                .border(1.dp, Color(0xFFEF4444).copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                                .clickable {
                                    viewModel.clearCache()
                                    Toast.makeText(context, "Cache cleared", Toast.LENGTH_SHORT).show()
                                }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "Clear",
                                color = Color(0xFFEF4444),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // ─────────────────────────────────────────────────────────────
                // SECTION 5 — SYSTEM
                // ─────────────────────────────────────────────────────────────
                SectionCard(title = "SYSTEM") {
                    // Wallpaper Picker shortcut
                    ActionRow(
                        icon = Icons.Default.Wallpaper,
                        gradient = listOf(Color(0xFF6366F1), Color(0xFF4338CA)),
                        title = "Open Wallpaper Picker",
                        subtitle = "Apply via Android system chooser",
                        onClick = { viewModel.openSystemWallpaperChooser(context) }
                    )

                    RowDivider()

                    // Architecture info block
                    RowHeader(
                        icon = Icons.Default.Info,
                        gradient = listOf(Color(0xFF64748B), Color(0xFF334155)),
                        title = "Engine Info",
                        subtitle = "Hardware decoding & rendering pipeline"
                    )
                    Column(
                        modifier = Modifier.padding(start = 68.dp, end = 16.dp, bottom = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        SpecRow("Version", "1.2 (Production)")
                        SpecRow("Video Backend", "Media3 ExoPlayer HW")
                        SpecRow("Idle Draw", "0% (Suspended off-screen)")
                    }
                }

                Spacer(Modifier.height(36.dp))
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// REUSABLE COMPONENTS
// ═══════════════════════════════════════════════════════════════════════════

@Composable
private fun OriginOsStatusCapsule(
    wallpaperTitle: String,
    wallpaperUri: String?,
    frameRate: Int,
    onOpenChooser: () -> Unit
) {
    val colors = AppTheme.colors
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(colors.surface)
            .border(1.dp, colors.border, RoundedCornerShape(22.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(colors.surfaceVariant)
                    .border(1.dp, colors.border, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (!wallpaperUri.isNullOrEmpty()) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(wallpaperUri)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Wallpaper,
                        contentDescription = null,
                        tint = colors.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981))
                    )
                    Text(
                        text = "ENGINE ACTIVE",
                        color = Color(0xFF10B981),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    )
                }
                Text(
                    text = wallpaperTitle,
                    color = colors.textPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    text = "$frameRate FPS · Zero Idle Drain",
                    color = colors.textMuted,
                    fontSize = 11.sp
                )
            }

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(colors.primary.copy(alpha = 0.12f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onOpenChooser() }
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.OpenInNew,
                    contentDescription = "Open Picker",
                    tint = colors.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable () -> Unit) {
    val colors = AppTheme.colors
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = title,
            color = colors.textMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(start = 10.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(colors.surface)
                .border(1.dp, colors.border, RoundedCornerShape(22.dp))
        ) {
            Column { content() }
        }
    }
}

@Composable
private fun RowDivider() {
    val colors = AppTheme.colors
    HorizontalDivider(
        color = colors.borderSubtle,
        modifier = Modifier.padding(start = 68.dp, end = 0.dp)
    )
}

@Composable
private fun RowHeader(
    icon: ImageVector,
    gradient: List<Color>,
    title: String,
    subtitle: String
) {
    val colors = AppTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(Brush.linearGradient(gradient)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
        Column {
            Text(
                text = title,
                color = colors.textPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Text(
                text = subtitle,
                color = colors.textSecondary,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun SwitchRow(
    icon: ImageVector,
    gradient: List<Color>,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val colors = AppTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onCheckedChange(!checked) }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(Brush.linearGradient(gradient)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = colors.textPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Text(
                text = subtitle,
                color = colors.textSecondary,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = colors.onPrimary,
                checkedTrackColor = colors.primary,
                uncheckedThumbColor = colors.textMuted,
                uncheckedTrackColor = colors.surfaceVariant
            )
        )
    }
}

@Composable
private fun ActionRow(
    icon: ImageVector,
    gradient: List<Color>,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    val colors = AppTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(Brush.linearGradient(gradient)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = colors.textPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text(text = subtitle, color = colors.textSecondary, fontSize = 12.sp)
        }
        Icon(
            imageVector = Icons.Default.OpenInNew,
            contentDescription = null,
            tint = colors.primary,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun ThreePillSelector(
    items: List<String>,
    icons: List<ImageVector>? = null,
    subtitles: List<String>? = null,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(colors.surfaceVariant)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items.forEachIndexed { index, label ->
            val isSelected = index == selectedIndex
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) colors.primary else Color.Transparent)
                    .clickable { onSelect(index) }
                    .padding(vertical = 9.dp),
                contentAlignment = Alignment.Center
            ) {
                if (subtitles != null) {
                    // FPS-style: two lines
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = label,
                            color = if (isSelected) colors.onPrimary else colors.textPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = subtitles[index],
                            color = if (isSelected) colors.onPrimary.copy(alpha = 0.8f) else colors.textMuted,
                            fontSize = 10.sp
                        )
                    }
                } else if (icons != null) {
                    // Theme-style: icon + label
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            imageVector = icons[index],
                            contentDescription = null,
                            tint = if (isSelected) colors.onPrimary else colors.textSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = label,
                            color = if (isSelected) colors.onPrimary else colors.textSecondary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    }
                } else {
                    Text(
                        text = label,
                        color = if (isSelected) colors.onPrimary else colors.textPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun AccentPicker(selectedIndex: Int, onSelect: (Int) -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppAccentPalettes.forEachIndexed { index, palette ->
            val isSelected = selectedIndex == index
            val scale by animateFloatAsState(
                targetValue = if (isSelected) 1.15f else 1.0f,
                animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing),
                label = "accentScale"
            )
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .scale(scale)
                    .clip(CircleShape)
                    .background(palette.darkPrimary)
                    .then(
                        if (isSelected) Modifier.border(2.5.dp, Color.White, CircleShape)
                        else Modifier
                    )
                    .clickable { onSelect(index) },
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = Color.Black,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SpecRow(label: String, value: String) {
    val colors = AppTheme.colors
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = colors.textMuted, fontSize = 12.sp)
        Text(text = value, color = colors.textPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}
