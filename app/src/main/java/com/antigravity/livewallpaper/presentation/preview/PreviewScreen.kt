package com.antigravity.livewallpaper.presentation.preview

import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.antigravity.livewallpaper.domain.model.Wallpaper
import com.antigravity.livewallpaper.domain.model.WallpaperType
import com.antigravity.livewallpaper.presentation.theme.AppTheme

@OptIn(UnstableApi::class)
@Composable
fun PreviewScreen(
    viewModel: PreviewViewModel,
    onNavigateBack: () -> Unit
) {
    val wallpaper by viewModel.wallpaper.collectAsState()
    val applyState by viewModel.applyState.collectAsState()
    val simulateIcons by viewModel.simulateIcons.collectAsState()
    val colors = AppTheme.colors

    var showSuccessDialog by remember { mutableStateOf(false) }

    LaunchedEffect(applyState) {
        if (applyState is ApplyUiState.Success) {
            showSuccessDialog = true
        }
    }

    Scaffold(
        containerColor = colors.background
    ) { paddingValues ->
        val currentWallpaper = wallpaper
        if (currentWallpaper == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = colors.primary)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Top Navigation Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
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

                // Simulate Icons Toggle Button (Tactile Pill)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (simulateIcons) colors.primary else colors.surface)
                        .border(1.dp, if (simulateIcons) colors.primary else colors.border, RoundedCornerShape(20.dp))
                        .clickable { viewModel.toggleSimulateIcons() }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Apps,
                            contentDescription = null,
                            tint = if (simulateIcons) colors.onPrimary else colors.textPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (simulateIcons) "Hide Icons" else "Test Launcher UI",
                            color = if (simulateIcons) colors.onPrimary else colors.textPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Realistic Phone Frame Preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                PhoneFramePreview(
                    wallpaper = currentWallpaper,
                    simulateIcons = simulateIcons
                )
            }

            // Details and Specs Panel
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Text(
                    text = currentWallpaper.title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = colors.textPrimary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = currentWallpaper.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Technical Specs Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SpecChip(
                        title = "Format",
                        value = currentWallpaper.type.name,
                        modifier = Modifier.weight(1f)
                    )
                    SpecChip(
                        title = "Resolution",
                        value = currentWallpaper.resolution,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Apply Button (Tactile Pill Button)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(colors.primary)
                        .clickable(enabled = applyState !is ApplyUiState.Applying) {
                            viewModel.applyWallpaper()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (applyState is ApplyUiState.Applying) {
                        CircularProgressIndicator(
                            color = colors.onPrimary,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.5.dp
                        )
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = colors.onPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Set Wallpaper",
                                color = colors.onPrimary,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                showSuccessDialog = false
                viewModel.resetApplyState()
            },
            title = {
                Text(
                    text = "Wallpaper Applied!",
                    color = colors.primary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Your live wallpaper has been activated.",
                    color = colors.textPrimary
                )
            },
            confirmButton = {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.primary)
                        .clickable {
                            showSuccessDialog = false
                            viewModel.resetApplyState()
                            onNavigateBack()
                        }
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Text("OK", color = colors.onPrimary, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = colors.surface,
            shape = RoundedCornerShape(24.dp)
        )
    }
}

@Composable
private fun PhoneFramePreview(
    wallpaper: Wallpaper,
    simulateIcons: Boolean
) {
    val colors = AppTheme.colors
    val frameShape = RoundedCornerShape(28.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(9f / 18f)
            .clip(frameShape)
            .background(Color.Black)
            .border(3.dp, colors.border, frameShape)
    ) {
        // Active Media Player or Image
        if (wallpaper.type == WallpaperType.VIDEO) {
            VideoPreviewPlayer(uriString = wallpaper.sourceUri)
        } else {
            val context = LocalContext.current
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(wallpaper.sourceUri)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Camera Punch Hole Mockup
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 10.dp)
                .size(12.dp)
                .clip(CircleShape)
                .background(Color.Black)
        )

        // Optional Simulated Launcher Overlay
        AnimatedVisibility(
            visible = simulateIcons,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            SimulatedHomeScreenOverlay()
        }
    }
}

@Composable
private fun VideoPreviewPlayer(uriString: String) {
    val context = LocalContext.current
    val exoPlayer = remember(uriString) {
        ExoPlayer.Builder(context).build().apply {
            repeatMode = Player.REPEAT_MODE_ALL
            volume = 0f
            videoScalingMode = androidx.media3.common.C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
            val uri = if (uriString.startsWith("asset:///")) {
                val path = uriString.removePrefix("asset:///")
                Uri.parse("file:///android_asset/$path")
            } else {
                Uri.parse(uriString)
            }
            setMediaItem(MediaItem.fromUri(uri))
            prepare()
            playWhenReady = true
        }
    }

    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer.release()
        }
    }

    AndroidView(
        factory = { ctx ->
            PlayerView(ctx).apply {
                player = exoPlayer
                useController = false
                resizeMode = androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_ZOOM
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
private fun SimulatedHomeScreenOverlay() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Clock & Date Widget
        Column(
            modifier = Modifier.padding(top = 28.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "09:41",
                color = Color.White,
                fontSize = 42.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "Monday, Sep 22 • 24°C Sunny",
                color = Color(0xE6FFFFFF),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }

        // Bottom Search Bar & Dock Icons
        Column(
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Fake Search Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0x59000000))
                    .border(0.5.dp, Color(0x33FFFFFF), RoundedCornerShape(24.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = Color(0xCCFFFFFF),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Search...",
                        color = Color(0x99FFFFFF),
                        fontSize = 12.sp
                    )
                }
            }

            // Fake Dock Icons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                MockAppIcon(icon = Icons.Default.Phone, label = "Phone", tint = Color(0xFF00E676))
                MockAppIcon(icon = Icons.Default.Mail, label = "Messages", tint = Color(0xFF2979FF))
                MockAppIcon(icon = Icons.Default.CameraAlt, label = "Camera", tint = Color(0xFFFF5252))
                MockAppIcon(icon = Icons.Default.Settings, label = "Settings", tint = Color(0xFFB0BEC5))
            }
        }
    }
}

@Composable
private fun MockAppIcon(icon: ImageVector, label: String, tint: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(Color(0xCC1A1F2C))
                .border(1.dp, Color(0x40FFFFFF), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(22.dp)
            )
        }
        Text(
            text = label,
            color = Color.White,
            fontSize = 9.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun SpecChip(
    title: String,
    value: String,
    valueColor: Color? = null,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(colors.surface)
            .border(1.dp, colors.border, RoundedCornerShape(14.dp))
            .padding(vertical = 12.dp, horizontal = 12.dp)
    ) {
        Column {
            Text(
                text = title,
                color = colors.textMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = value,
                color = valueColor ?: colors.textPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
