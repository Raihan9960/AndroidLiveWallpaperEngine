package com.antigravity.livewallpaper.service

import android.app.WallpaperManager
import android.os.Build
import android.service.wallpaper.WallpaperService
import android.util.Log
import android.view.SurfaceHolder
import com.antigravity.livewallpaper.domain.model.AppSettings
import com.antigravity.livewallpaper.domain.model.Wallpaper
import com.antigravity.livewallpaper.domain.model.WallpaperType
import com.antigravity.livewallpaper.domain.repository.WallpaperRepository
import com.antigravity.livewallpaper.service.engine.WallpaperRenderer
import com.antigravity.livewallpaper.service.engine.renderers.GifWallpaperRenderer
import com.antigravity.livewallpaper.service.engine.renderers.ImageWallpaperRenderer
import com.antigravity.livewallpaper.service.engine.renderers.VideoWallpaperRenderer
import com.antigravity.livewallpaper.service.power.PowerStateMonitor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LiveWallpaperService : WallpaperService() {

    companion object {
        private const val TAG = "LiveWallpaperService"
    }

    @Inject
    lateinit var repository: WallpaperRepository

    override fun onCreateEngine(): Engine {
        return LiveWallpaperEngine()
    }

    inner class LiveWallpaperEngine : Engine() {
        private var renderer: WallpaperRenderer? = null
        private var powerStateMonitor: PowerStateMonitor? = null
        private val engineScope = CoroutineScope(Dispatchers.Main + Job())
        private var currentWallpaper: Wallpaper? = null
        private var currentSettings: AppSettings = AppSettings()
        private var currentHolder: SurfaceHolder? = null

        override fun onCreate(surfaceHolder: SurfaceHolder) {
            super.onCreate(surfaceHolder)
            currentHolder = surfaceHolder

            engineScope.launch {
                repository.getActiveWallpaper().collectLatest { wallpaper ->
                    if (wallpaper != null && wallpaper != currentWallpaper) {
                        currentWallpaper = wallpaper
                        currentHolder?.let { holder ->
                            switchRenderer(holder, wallpaper)
                        }
                    }
                }
            }

            engineScope.launch {
                repository.getSettings().collectLatest { settings ->
                    currentSettings = settings
                    renderer?.setFrameRateCap(settings.frameRateCap)
                    if (!settings.batterySaverAdaptive) {
                        renderer?.setPowerSaveMode(false)
                    }
                }
            }

            powerStateMonitor = PowerStateMonitor(applicationContext) { isPowerSave ->
                if (currentSettings.batterySaverAdaptive) {
                    renderer?.setPowerSaveMode(isPowerSave)
                }
            }.apply { start() }
        }

        private fun switchRenderer(holder: SurfaceHolder, wallpaper: Wallpaper) {
            Log.d(TAG, "Switching renderer to ${wallpaper.type} (${wallpaper.title})")
            renderer?.release()

            renderer = when (wallpaper.type) {
                WallpaperType.VIDEO -> VideoWallpaperRenderer(applicationContext, wallpaper.sourceUri)
                WallpaperType.GIF -> GifWallpaperRenderer(applicationContext, wallpaper.sourceUri)
                WallpaperType.IMAGE -> ImageWallpaperRenderer(applicationContext, wallpaper.sourceUri)
            }

            renderer?.setFrameRateCap(currentSettings.frameRateCap)
            if (currentSettings.batterySaverAdaptive) {
                renderer?.setPowerSaveMode(powerStateMonitor?.isPowerSaveMode?.value ?: false)
            }
            renderer?.onSurfaceCreated(holder)
            renderer?.onVisibilityChanged(isVisible)
        }

        override fun onSurfaceCreated(holder: SurfaceHolder) {
            super.onSurfaceCreated(holder)
            currentHolder = holder
            renderer?.onSurfaceCreated(holder)
        }

        override fun onSurfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)
            currentHolder = holder
            renderer?.onSurfaceChanged(holder, format, width, height)
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            super.onSurfaceDestroyed(holder)
            renderer?.onSurfaceDestroyed(holder)
            currentHolder = null
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            Log.d(TAG, "onVisibilityChanged: visible=$visible")
            renderer?.onVisibilityChanged(visible)
        }

        override fun onOffsetsChanged(
            xOffset: Float,
            yOffset: Float,
            xOffsetStep: Float,
            yOffsetStep: Float,
            xPixelOffset: Int,
            yPixelOffset: Int
        ) {
            super.onOffsetsChanged(xOffset, yOffset, xOffsetStep, yOffsetStep, xPixelOffset, yPixelOffset)
            if (currentSettings.parallaxEnabled) {
                renderer?.onOffsetsChanged(xOffset, yOffset, xOffsetStep, yOffsetStep, xPixelOffset, yPixelOffset)
            }
        }

        override fun onWallpaperFlagsChanged(which: Int) {
            super.onWallpaperFlagsChanged(which)
            val scopeStr = when (which) {
                WallpaperManager.FLAG_SYSTEM -> "HOME"
                WallpaperManager.FLAG_LOCK -> "LOCK"
                WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK -> "BOTH"
                else -> "FLAG_$which"
            }
            Log.d(TAG, "onWallpaperFlagsChanged: surface=$scopeStr")
        }

        override fun onDestroy() {
            super.onDestroy()
            powerStateMonitor?.stop()
            renderer?.release()
            renderer = null
            engineScope.cancel()
        }
    }
}
