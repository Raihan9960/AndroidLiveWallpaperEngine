package com.antigravity.livewallpaper.presentation.onboarding

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.BatterySaver
import androidx.compose.material.icons.filled.LockOpen
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
import com.antigravity.livewallpaper.presentation.theme.DarkBackground
import com.antigravity.livewallpaper.presentation.theme.DarkBorder
import com.antigravity.livewallpaper.presentation.theme.DarkSurface
import com.antigravity.livewallpaper.presentation.theme.DarkSurfaceVariant
import com.antigravity.livewallpaper.presentation.theme.NeonCyan
import com.antigravity.livewallpaper.presentation.theme.NeonEmerald
import com.antigravity.livewallpaper.presentation.theme.NeonPink
import com.antigravity.livewallpaper.presentation.theme.TextMuted
import com.antigravity.livewallpaper.presentation.theme.TextPrimary
import com.antigravity.livewallpaper.presentation.theme.TextSecondary

data class OnboardingPage(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val accentColor: Color
)

@Composable
fun OnboardingScreen(
    onFinishOnboarding: () -> Unit
) {
    val pages = listOf(
        OnboardingPage(
            title = "Zero-Drain Battery Architecture",
            description = "Engineered with strict visibility guards. The instant your screen locks or any app opens, playback suspends to guarantee 0% idle drain.",
            icon = Icons.Default.BatterySaver,
            accentColor = NeonEmerald
        ),
        OnboardingPage(
            title = "Home Screen Focused",
            description = "Applies specifically to your Home Screen without overriding your Lock Screen photo. Your personal lock screen remains 100% untouched.",
            icon = Icons.Default.LockOpen,
            accentColor = NeonCyan
        ),
        OnboardingPage(
            title = "Import Any Video or GIF",
            description = "Bring any personal MP4, WebM, animated GIF, or high-res photo. Hardware-accelerated decoding keeps playback fluid at full display resolution.",
            icon = Icons.Default.MovieCreation,
            accentColor = NeonPink
        )
    )

    var currentPage by remember { mutableIntStateOf(0) }

    Scaffold(
        containerColor = DarkBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(28.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Skip Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (currentPage < pages.size - 1) {
                    Text(
                        text = "Skip",
                        color = TextMuted,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .clickable { onFinishOnboarding() }
                            .padding(8.dp)
                    )
                }
            }

            // Center Visual & Text
            val page = pages[currentPage]
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Large Glow Icon
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(DarkSurfaceVariant)
                        .border(2.dp, page.accentColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = page.icon,
                        contentDescription = null,
                        tint = page.accentColor,
                        modifier = Modifier.size(54.dp)
                    )
                }

                Text(
                    text = page.title,
                    style = MaterialTheme.typography.headlineLarge,
                    color = TextPrimary,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = page.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary,
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
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    pages.indices.forEach { index ->
                        val isSelected = index == currentPage
                        Box(
                            modifier = Modifier
                                .height(8.dp)
                                .width(if (isSelected) 28.dp else 8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (isSelected) NeonCyan else DarkSurfaceVariant)
                        )
                    }
                }

                // Action Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(NeonCyan, Color(0xFF00B4D8))
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
                            color = DarkBackground,
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null,
                            tint = DarkBackground,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
