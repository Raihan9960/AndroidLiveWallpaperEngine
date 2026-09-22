package com.antigravity.livewallpaper.presentation.onboarding

import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.BatterySaver
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.MovieCreation
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.antigravity.livewallpaper.presentation.theme.AppTheme

data class OnboardingPage(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val darkAccent: Color,
    val lightAccent: Color
)

@Composable
fun OnboardingScreen(
    onFinishOnboarding: () -> Unit
) {
    val colors = AppTheme.colors

    val pages = remember {
        listOf(
            OnboardingPage(
                title = "Zero-Drain Battery Architecture",
                description = "Engineered with strict visibility guards. The instant your screen locks or any app opens, playback suspends to guarantee 0% idle drain.",
                icon = Icons.Default.BatterySaver,
                darkAccent = Color(0xFF00F5A0),
                lightAccent = Color(0xFF0D9488)
            ),
            OnboardingPage(
                title = "Hardware Accelerated",
                description = "Smooth 60 FPS rendering powered by ExoPlayer hardware codecs, optimized for seamless scrolling and low latency.",
                icon = Icons.Default.Speed,
                darkAccent = Color(0xFF00E5FF),
                lightAccent = Color(0xFF0D5C68)
            ),
            OnboardingPage(
                title = "Import Any Video or GIF",
                description = "Bring any personal MP4, WebM, animated GIF, or high-res photo. Hardware-accelerated decoding keeps playback fluid at full display resolution.",
                icon = Icons.Default.MovieCreation,
                darkAccent = Color(0xFFFF007F),
                lightAccent = Color(0xFFBE185D)
            )
        )
    }

    var currentPage by remember { mutableIntStateOf(0) }

    Scaffold(
        containerColor = colors.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 28.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Skip Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (currentPage < pages.size - 1) {
                    Text(
                        text = "Skip",
                        color = colors.textMuted,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        modifier = Modifier
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onFinishOnboarding() }
                            .padding(8.dp)
                    )
                }
            }

            // Center Visual & Text
            val page = pages[currentPage]
            val accentColor = if (colors.isDark) page.darkAccent else page.lightAccent

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                // Large Tactile Icon
                Box(
                    modifier = Modifier
                        .size(116.dp)
                        .clip(CircleShape)
                        .background(colors.surface)
                        .border(2.dp, accentColor.copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = page.icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(54.dp)
                    )
                }

                Text(
                    text = page.title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = colors.textPrimary,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = page.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = colors.textSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )
            }

            // Bottom Navigation & Dots
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Page Indicator Dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    pages.indices.forEach { index ->
                        val isSelected = index == currentPage
                        Box(
                            modifier = Modifier
                                .height(8.dp)
                                .width(if (isSelected) 28.dp else 8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    if (isSelected) colors.primary else colors.border
                                )
                                .animateContentSize()
                        )
                    }
                }

                // Tactile Action Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(colors.primary, colors.primaryVariant)
                            )
                        )
                        .clickable {
                            if (currentPage < pages.size - 1) {
                                currentPage++
                            } else {
                                onFinishOnboarding()
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (currentPage < pages.size - 1) "Continue" else "Get Started",
                            color = colors.onPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null,
                            tint = colors.onPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
