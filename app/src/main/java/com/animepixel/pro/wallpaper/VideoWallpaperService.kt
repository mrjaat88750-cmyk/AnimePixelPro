
package com.animepixel.pro.wallpaper

import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import android.media.MediaPlayer
import android.net.Uri
import com.animepixel.pro.R

class VideoWallpaperService : WallpaperService() {

    override fun onCreateEngine(): Engine {
        return VideoEngine()
    }

    inner class VideoEngine : Engine() {

        private var mediaPlayer: MediaPlayer? = null
        private var visible = false

        private val videoResId: Int =
            getSharedPreferences("wallpaper_prefs", MODE_PRIVATE)
                .getInt("selected_video_res", R.raw.wolf_blue_red)

        override fun onSurfaceCreated(holder: SurfaceHolder) {
            super.onSurfaceCreated(holder)
            if (visible) startVideo(holder)
        }

        override fun onVisibilityChanged(isVisible: Boolean) {
            visible = isVisible

            if (visible) {
                startVideo(surfaceHolder)
            } else {
                stopVideo()
            }
        }

        private fun startVideo(holder: SurfaceHolder) {
            if (!visible || !holder.surface.isValid) return

            stopVideo()

            try {
                val player = MediaPlayer()
                mediaPlayer = player

                player.setDataSource(
                    this@VideoWallpaperService,
                    Uri.parse(
                        "android.resource://$packageName/$videoResId"
                    )
                )

                player.setSurface(holder.surface)
                player.isLooping = true
                player.setVolume(0f, 0f)

                player.setOnPreparedListener {
                    if (visible && mediaPlayer === it) {
                        it.start()
                    }
                }

                player.setOnErrorListener { _, _, _ ->
                    stopVideo()
                    true
                }

                player.prepareAsync()

            } catch (e: Exception) {
                stopVideo()
            }
        }

        private fun stopVideo() {
            val player = mediaPlayer
            mediaPlayer = null

            try {
                player?.setOnPreparedListener(null)
                player?.setOnErrorListener(null)
                player?.release()
            } catch (_: Exception) {
            }
        }

        override fun onSurfaceChanged(
            holder: SurfaceHolder,
            format: Int,
            width: Int,
            height: Int
        ) {
            super.onSurfaceChanged(holder, format, width, height)
            if (visible) startVideo(holder)
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            visible = false
            stopVideo()
            super.onSurfaceDestroyed(holder)
        }

        override fun onDestroy() {
            stopVideo()
            super.onDestroy()
        }
    }
}

