package com.antigravity.livewallpaper.service.engine.renderers

import android.content.Context
import android.graphics.Canvas
import android.graphics.Movie
import android.graphics.Paint
import android.net.Uri
import android.os.SystemClock
import android.util.Log
import android.view.SurfaceHolder
import com.antigravity.livewallpaper.service.engine.WallpaperRenderer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.InputStream

@Suppress("DEPRECATION")
class GifWallpaperRenderer(
    private val context: Context,
    private val gifUriString: String
) : WallpaperRenderer {

    companion object {
        private const val TAG = "GifWallpaperRenderer"
    }

    private val scope = CoroutineScope(Dispatchers.Default + Job())
    private var renderJob: Job? = null

    private var movie: Movie? = null
    private var movieDuration: Int = 1000
    private var startTime: Long = 0L

    private var surfaceWidth: Int = 1080
    private var surfaceHeight: Int = 1920
    private var isSurfaceCreated = false
    private var isVisible = true
    private var frameRateCap = 30
    private var isPowerSaveMode = false

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    private var currentHolder: SurfaceHolder? = null

    init {
        loadGif()
    }

    private fun loadGif() {
        scope.launch {
            try {
                val bytes = withContext(Dispatchers.IO) {
                    openInputStream(gifUriString)?.use { stream ->
                        val buffer = ByteArrayOutputStream()
                        stream.copyTo(buffer)
                        buffer.toByteArray()
                    }
                }
                if (bytes != null && bytes.isNotEmpty()) {
                    movie = Movie.decodeByteArray(bytes, 0, bytes.size)
                    val dur = movie?.duration() ?: 1000
                    movieDuration = if (dur > 0) dur else 1000
                    startTime = SystemClock.uptimeMillis()

                    if (isVisible && isSurfaceCreated) {
                        startRenderLoop()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading GIF", e)
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

    private fun startRenderLoop() {
        renderJob?.cancel()
        renderJob = scope.launch {
            val targetFps = if (isPowerSaveMode) 15 else frameRateCap.coerceIn(15, 60)
            val frameDelay = (1000L / targetFps).coerceAtLeast(16L)

            while (isActive && isVisible && isSurfaceCreated) {
                drawFrame()
                delay(frameDelay)
            }
        }
    }

    private fun stopRenderLoop() {
        renderJob?.cancel()
        renderJob = null
    }

    private fun drawFrame() {
        val m = movie ?: return
        val holder = currentHolder ?: return
        if (!isSurfaceCreated || !isVisible) return

        var canvas: Canvas? = null
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
                val now = SystemClock.uptimeMillis()
                val relTime = ((now - startTime) % movieDuration).toInt()
                m.setTime(relTime)

                val movieWidth = if (m.width() > 0) m.width().toFloat() else surfaceWidth.toFloat()
                val movieHeight = if (m.height() > 0) m.height().toFloat() else surfaceHeight.toFloat()

                val scale = maxOf(
                    surfaceWidth.toFloat() / movieWidth,
                    surfaceHeight.toFloat() / movieHeight
                )

                val scaledWidth = movieWidth * scale
                val scaledHeight = movieHeight * scale
                val dx = (surfaceWidth - scaledWidth) / 2f
                val dy = (surfaceHeight - scaledHeight) / 2f

                canvas.save()
                canvas.translate(dx, dy)
                canvas.scale(scale, scale)
                m.draw(canvas, 0f, 0f, paint)
                canvas.restore()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error rendering GIF frame", e)
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

    override fun onSurfaceCreated(holder: SurfaceHolder) {
        currentHolder = holder
        isSurfaceCreated = true
        if (isVisible && movie != null) {
            startRenderLoop()
        }
    }

    override fun onSurfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        currentHolder = holder
        surfaceWidth = width
        surfaceHeight = height
    }

    override fun onSurfaceDestroyed(holder: SurfaceHolder) {
        isSurfaceCreated = false
        currentHolder = null
        stopRenderLoop()
    }

    override fun onVisibilityChanged(visible: Boolean) {
        isVisible = visible
        if (visible && isSurfaceCreated && movie != null) {
            startRenderLoop()
        } else {
            // Immediate full stop of render loop to ensure zero background CPU usage
            stopRenderLoop()
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
    }

    override fun setFrameRateCap(fps: Int) {
        this.frameRateCap = fps
        if (isVisible && isSurfaceCreated) {
            startRenderLoop()
        }
    }

    override fun setPowerSaveMode(enabled: Boolean) {
        this.isPowerSaveMode = enabled
        if (isVisible && isSurfaceCreated) {
            startRenderLoop()
        }
    }

    override fun release() {
        stopRenderLoop()
        movie = null
        currentHolder = null
    }
}
