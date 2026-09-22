package com.antigravity.livewallpaper.service.engine

import android.view.SurfaceHolder

interface WallpaperRenderer {
    fun onSurfaceCreated(holder: SurfaceHolder)
    fun onSurfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int)
    fun onSurfaceDestroyed(holder: SurfaceHolder)
    fun onVisibilityChanged(visible: Boolean)
    fun onOffsetsChanged(
        xOffset: Float,
        yOffset: Float,
        xOffsetStep: Float,
        yOffsetStep: Float,
        xPixelOffset: Int,
        yPixelOffset: Int
    )
    fun setFrameRateCap(fps: Int)
    fun setPowerSaveMode(enabled: Boolean)
    fun release()
}
