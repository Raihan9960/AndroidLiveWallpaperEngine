package com.antigravity.livewallpaper.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Wallpaper
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.antigravity.livewallpaper.presentation.theme.DarkBackground
import com.antigravity.livewallpaper.presentation.theme.DarkBorder
import com.antigravity.livewallpaper.presentation.theme.DarkSurface
import com.antigravity.livewallpaper.presentation.theme.DarkSurfaceVariant
import com.antigravity.livewallpaper.presentation.theme.NeonCyan
import com.antigravity.livewallpaper.presentation.theme.NeonEmerald
import com.antigravity.livewallpaper.presentation.theme.TextMuted
import com.antigravity.livewallpaper.presentation.theme.TextPrimary
import com.antigravity.livewallpaper.presentation.theme.TextSecondary

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit
) {
    val settings by viewModel.settings.collectAsState()
    val cacheSize by viewModel.cacheSize.collectAsState()

    Scaffold(
        containerColor = DarkBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(DarkSurfaceVariant)
                        .border(1.dp, DarkBorder, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }

                Text(
                    text = "Engine Settings",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Section 1: Frame Rate Limit
                SettingsSection(
                    icon = Icons.Default.Speed,
                    title = "Frame Rate Cap",
                    subtitle = "Limits maximum render frequency to conserve battery"
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FpsOption(
                            label = "24 FPS",
                            description = "Eco",
                            isSelected = settings.frameRateCap == 24,
                            onClick = { viewModel.setFrameRateCap(24) },
                            modifier = Modifier.weight(1f)
                        )
                        FpsOption(
                            label = "30 FPS",
                            description = "Default",
                            isSelected = settings.frameRateCap == 30,
                            onClick = { viewModel.setFrameRateCap(30) },
                            modifier = Modifier.weight(1f)
                        )
                        FpsOption(
                            label = "60 FPS",
                            description = "Smooth",
                            isSelected = settings.frameRateCap == 60,
                            onClick = { viewModel.setFrameRateCap(60) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Section 2: Battery Saver Adaptive Mode
                SettingsSection(
                    icon = Icons.Default.BatterySaver,
                    title = "Battery Saver Adaptive Mode",
                    subtitle = "Automatically slows or suspends video decoding when system Battery Saver is active"
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (settings.batterySaverAdaptive) "Adaptive Mode Enabled" else "Adaptive Mode Disabled",
                            color = if (settings.batterySaverAdaptive) NeonEmerald else TextSecondary,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                        Switch(
                            checked = settings.batterySaverAdaptive,
                            onCheckedChange = { viewModel.setBatterySaverAdaptive(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = NeonCyan,
                                checkedTrackColor = Color(0xFF0F3A47),
                                uncheckedThumbColor = TextMuted,
                                uncheckedTrackColor = DarkSurfaceVariant
                            )
                        )
                    }
                }

                // Section 3: Parallax
                SettingsSection(
                    icon = Icons.Default.PanTool,
                    title = "Home Screen Parallax Pan",
                    subtitle = "Smoothly shifts image/video viewport as you swipe between launcher pages"
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (settings.parallaxEnabled) "Parallax Enabled" else "Parallax Disabled",
                            color = if (settings.parallaxEnabled) NeonCyan else TextSecondary,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                        Switch(
                            checked = settings.parallaxEnabled,
                            onCheckedChange = { viewModel.setParallaxEnabled(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = NeonCyan,
                                checkedTrackColor = Color(0xFF0F3A47),
                                uncheckedThumbColor = TextMuted,
                                uncheckedTrackColor = DarkSurfaceVariant
                            )
                        )
                    }
                }

                // Section 4: Cache Management
                SettingsSection(
                    icon = Icons.Default.CleaningServices,
                    title = "Storage & Transcode Cache",
                    subtitle = "Cached optimized videos for fast stutter-free playback"
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Current Cache Usage",
                                color = TextMuted,
                                fontSize = 12.sp
                            )
                            Text(
                                text = cacheSize,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(DarkSurfaceVariant)
                                .border(1.dp, DarkBorder, RoundedCornerShape(10.dp))
                                .clickable { viewModel.clearCache() }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "Clear Cache",
                                color = Color(0xFFFF5252),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Section 5: Home Screen Isolation Guarantee
                SettingsSection(
                    icon = Icons.Default.Wallpaper,
                    title = "Lock Screen Protection",
                    subtitle = "System Live Wallpaper policy"
                ) {
                    Text(
                        text = "Lively Engine is engineered specifically for your Home Screen. When applying, the system dialog will offer Home Screen application to guarantee your lock screen wallpaper remains completely unchanged.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }

                // Section 6: About
                SettingsSection(
                    icon = Icons.Default.Info,
                    title = "Engine Architecture",
                    subtitle = "Production release specification"
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Engine Version", color = TextMuted, fontSize = 12.sp)
                            Text("1.0.0 (Production)", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Playback Backend", color = TextMuted, fontSize = 12.sp)
                            Text("Media3 ExoPlayer HW", color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Background Draw", color = TextMuted, fontSize = 12.sp)
                            Text("0% (Suspended off-screen)", color = NeonEmerald, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun SettingsSection(
    icon: ImageVector,
    title: String,
    subtitle: String,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(DarkSurface)
            .border(1.dp, DarkBorder, RoundedCornerShape(18.dp))
            .padding(18.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(DarkSurfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            content()
        }
    }
}

@Composable
private fun FpsOption(
    label: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) Color(0x3300F0FF) else DarkSurfaceVariant)
            .border(1.dp, if (isSelected) NeonCyan else DarkBorder, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = label,
                color = if (isSelected) NeonCyan else TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                text = description,
                color = if (isSelected) NeonCyan else TextMuted,
                fontSize = 10.sp
            )
        }
    }
}
