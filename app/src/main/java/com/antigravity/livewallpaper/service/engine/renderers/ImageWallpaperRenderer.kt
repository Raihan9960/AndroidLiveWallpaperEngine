package com.antigravity.livewallpaper.service.engine.renderers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.net.Uri
import android.util.Log
import android.view.SurfaceHolder
import com.antigravity.livewallpaper.service.engine.WallpaperRenderer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream

class ImageWallpaperRenderer(
    private val context: Context,
    private val imageUriString: String
) : WallpaperRenderer {

    companion object {
        private const val TAG = "ImageWallpaperRenderer"
    }

    private val scope = CoroutineScope(Dispatchers.Default + Job())
    private var cachedBitmap: Bitmap? = null
    private var surfaceWidth: Int = 1080
    private var surfaceHeight: Int = 1920
    private var currentXOffset: Float = 0.5f
    private var isSurfaceCreated = false
    private var isVisible = true
    private var parallaxEnabled = true

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    private val matrix = Matrix()

    init {
        loadBitmap()
    }

    private fun loadBitmap() {
        scope.launch {
            try {
                val bitmap = withContext(Dispatchers.IO) {
                    openInputStream(imageUriString)?.use { stream ->
                        BitmapFactory.decodeStream(stream)
                    }
                }
                cachedBitmap = bitmap
                if (isSurfaceCreated && isVisible) {
                    drawFrame()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading wallpaper image", e)
            }
        }
    }

    private fun openInputStream(uriString: String): InputStream? {
        return when {
            uriString.startsWith("asset:///") -> {
                val assetPath = uriString.removePrefix("asset:///")
                context.assets.open(assetPath)
            }
            uriString.startsWith("file://") -> {
                val fileUri = Uri.parse(uriString)
                fileUri.path?.let { java.io.File(it).inputStream() }
            }
            uriString.startsWith("content://") -> {
                context.contentResolver.openInputStream(Uri.parse(uriString))
            }
            else -> {
                context.assets.open(uriString)
            }
        }
    }

    private fun drawFrame() {
        val bitmap = cachedBitmap ?: return
        if (!isSurfaceCreated || !isVisible) return

        var canvas: Canvas? = null
        val holder = currentHolder ?: return

        try {
            canvas = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                try {
                    holder.lockHardwareCanvas()
                } catch (e: Exception) {
                    holder.lockCanvas()
                }
            } else {
                holder.lockCanvas()
            }

            if (canvas != null) {
                matrix.reset()
                val scale = maxOf(
                    surfaceWidth.toFloat() / bitmap.width.toFloat(),
                    surfaceHeight.toFloat() / bitmap.height.toFloat()
                ) * 1.15f // Slight 15% zoom buffer to allow smooth parallax pan

                val scaledWidth = bitmap.width * scale
                val scaledHeight = bitmap.height * scale

                // Parallax shift based on home screen page swipe
                val maxShiftX = scaledWidth - surfaceWidth
                val shiftX = if (parallaxEnabled) -maxShiftX * currentXOffset else -maxShiftX * 0.5f
                val shiftY = -(scaledHeight - surfaceHeight) / 2f

                matrix.postScale(scale, scale)
                matrix.postTranslate(shiftX, shiftY)

                canvas.drawBitmap(bitmap, matrix, paint)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error drawing image frame", e)
        } finally {
            if (canvas != null) {
                try {
                    holder.unlockCanvasAndPost(canvas)
                } catch (e: Exception) {
                    Log.e(TAG, "Error unlocking canvas", e)
                }
            }
        }
    }

    private var currentHolder: SurfaceHolder? = null

    override fun onSurfaceCreated(holder: SurfaceHolder) {
        currentHolder = holder
        isSurfaceCreated = true
        drawFrame()
    }

    override fun onSurfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        currentHolder = holder
        surfaceWidth = width
        surfaceHeight = height
        drawFrame()
    }

    override fun onSurfaceDestroyed(holder: SurfaceHolder) {
        isSurfaceCreated = false
        currentHolder = null
    }

    override fun onVisibilityChanged(visible: Boolean) {
        isVisible = visible
        if (visible && isSurfaceCreated) {
            drawFrame()
        }
    }

    override fun onOffsetsChanged(
        xOffset: Float,
        yOffset: Float,
        xOffsetStep: Float,
        yOffsetStep: Float,
        xPixelOffset: Int,
        yPixelOffset: Int
    ) {
        if (parallaxEnabled && currentXOffset != xOffset) {
            currentXOffset = xOffset
            if (isVisible && isSurfaceCreated) {
                drawFrame()
            }
        }
    }

    override fun setFrameRateCap(fps: Int) {
        // Static image has no recurring frame rate; near-zero power draw
    }

    override fun setPowerSaveMode(enabled: Boolean) {
        if (enabled) {
            parallaxEnabled = false
        } else {
            parallaxEnabled = true
        }
    }

    override fun release() {
        cachedBitmap?.recycle()
        cachedBitmap = null
        currentHolder = null
    }
}
