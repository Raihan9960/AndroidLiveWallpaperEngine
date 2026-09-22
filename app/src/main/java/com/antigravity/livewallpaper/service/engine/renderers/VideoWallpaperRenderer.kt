package com.antigravity.livewallpaper.service.engine.renderers

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.SurfaceHolder
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.antigravity.livewallpaper.service.engine.WallpaperRenderer

class VideoWallpaperRenderer(
    private val context: Context,
    private val mediaUriString: String
) : WallpaperRenderer {

    companion object {
        private const val TAG = "VideoWallpaperRenderer"
    }

    private var player: ExoPlayer? = null
    private var isSurfaceCreated = false
    private var isVisible = false
    private var frameRateCap: Int = 30
    private var isPowerSaveMode: Boolean = false

    private fun initPlayer(holder: SurfaceHolder) {
        if (player != null) return

        try {
            val exoPlayer = ExoPlayer.Builder(context).build().apply {
                repeatMode = Player.REPEAT_MODE_ALL
                volume = 0f
                videoScalingMode = androidx.media3.common.C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
                setVideoSurfaceHolder(holder)

                val uri = parseMediaUri(mediaUriString)
                val mediaItem = MediaItem.fromUri(uri)
                setMediaItem(mediaItem)
                prepare()
            }
            player = exoPlayer
            applyPlaybackSpeedOrFramerate()

            if (isVisible) {
                exoPlayer.playWhenReady = true
                exoPlayer.play()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing ExoPlayer", e)
        }
    }

    private fun parseMediaUri(uriString: String): Uri {
        return if (uriString.startsWith("asset:///")) {
            val assetRelativePath = uriString.removePrefix("asset:///")
            Uri.parse("file:///android_asset/$assetRelativePath")
        } else {
            Uri.parse(uriString)
        }
    }

    override fun onSurfaceCreated(holder: SurfaceHolder) {
        isSurfaceCreated = true
        initPlayer(holder)
    }

    override fun onSurfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        player?.setVideoSurfaceHolder(holder)
    }

    override fun onSurfaceDestroyed(holder: SurfaceHolder) {
        isSurfaceCreated = false
        player?.clearVideoSurfaceHolder(holder)
        player?.pause()
    }

    override fun onVisibilityChanged(visible: Boolean) {
        isVisible = visible
        val p = player ?: return

        if (visible && isSurfaceCreated) {
            if (isPowerSaveMode) {
                // In battery saver, drop playback speed or play every other frame to conserve GPU/CPU
                p.playbackParameters = PlaybackParameters(0.85f)
            } else {
                p.playbackParameters = PlaybackParameters(1.0f)
            }
            p.playWhenReady = true
            p.play()
        } else {
            // Hard pause immediately when off-screen to guarantee zero CPU/GPU drain
            p.pause()
            p.playWhenReady = false
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
        // Offset parallax hook
    }

    override fun setFrameRateCap(fps: Int) {
        this.frameRateCap = fps
        applyPlaybackSpeedOrFramerate()
    }

    override fun setPowerSaveMode(enabled: Boolean) {
        this.isPowerSaveMode = enabled
        if (isVisible && player != null) {
            if (enabled) {
                player?.playbackParameters = PlaybackParameters(0.85f)
            } else {
                player?.playbackParameters = PlaybackParameters(1.0f)
            }
        }
    }

    private fun applyPlaybackSpeedOrFramerate() {
        // Can adjust player playback parameters or decoder configuration
    }

    override fun release() {
        try {
            player?.stop()
            player?.release()
            player = null
        } catch (e: Exception) {
            Log.e(TAG, "Error releasing ExoPlayer", e)
        }
    }
}
