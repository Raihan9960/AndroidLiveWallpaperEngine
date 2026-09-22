package com.antigravity.livewallpaper.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.antigravity.livewallpaper.presentation.gallery.GalleryScreen
import com.antigravity.livewallpaper.presentation.gallery.GalleryViewModel
import com.antigravity.livewallpaper.presentation.onboarding.OnboardingScreen
import com.antigravity.livewallpaper.presentation.preview.PreviewScreen
import com.antigravity.livewallpaper.presentation.preview.PreviewViewModel
import com.antigravity.livewallpaper.presentation.settings.SettingsScreen
import com.antigravity.livewallpaper.presentation.settings.SettingsViewModel

@Composable
fun LiveWallpaperNavHost(
    navController: NavHostController,
    startDestination: String = Screen.Gallery.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(durationMillis = 340, easing = FastOutSlowInEasing)
            ) + fadeIn(animationSpec = tween(durationMillis = 300))
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                targetOffset = { fullWidth -> fullWidth / 3 },
                animationSpec = tween(durationMillis = 340, easing = FastOutSlowInEasing)
            ) + fadeOut(animationSpec = tween(durationMillis = 260))
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                initialOffset = { fullWidth -> fullWidth / 3 },
                animationSpec = tween(durationMillis = 340, easing = FastOutSlowInEasing)
            ) + fadeIn(animationSpec = tween(durationMillis = 300))
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(durationMillis = 340, easing = FastOutSlowInEasing)
            ) + fadeOut(animationSpec = tween(durationMillis = 260))
        }
    ) {
        composable(Screen.Gallery.route) {
            val viewModel: GalleryViewModel = hiltViewModel()
            GalleryScreen(
                viewModel = viewModel,
                onNavigateToPreview = { wallpaperId ->
                    navController.navigate(Screen.Preview.createRoute(wallpaperId))
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        composable(
            route = Screen.Preview.route,
            arguments = listOf(
                navArgument("wallpaperId") { type = NavType.StringType }
            )
        ) {
            val viewModel: PreviewViewModel = hiltViewModel()
            PreviewScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Settings.route) {
            val viewModel: SettingsViewModel = hiltViewModel()
            SettingsScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onFinishOnboarding = {
                    navController.navigate(Screen.Gallery.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
