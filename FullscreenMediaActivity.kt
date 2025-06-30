package com.forgebyte.tech.simplegallery

import android.R.attr.scaleType
import android.graphics.Color
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.VideoView
import androidx.annotation.RequiresApi
import
 androidx.appcompat.app.AppCompatActivity

import com.bumptech.glide.Glide
import kotlin.jvm.java

class FullscreenMediaActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_URI = "extra_uri"
        const val EXTRA_IS_VIDEO = "extra_is_video"
    }

    private lateinit var imageView: ImageView
    private lateinit var videoView: VideoView
    private var isMuted = true
    private var isPlaying = false

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        supportActionBar?.hide()

        val isVideo = intent.getBooleanExtra(EXTRA_IS_VIDEO, false)
        val uri = intent.getParcelableExtra(EXTRA_URI, Uri::class.java) ?: return finish()

        if (isVideo) {
            // Show VideoView fullscreen
            videoView = VideoView(this)
            setContentView(videoView)

            videoView.setVideoURI(uri)
            videoView.setOnPreparedListener { mp ->
                mp.isLooping = true
                mp.setVolume(0f, 0f) // Start muted
                videoView.start()
                isPlaying = true
            }

            videoView.setOnClickListener {
                val mp = videoView.mediaPlayer ?: return@setOnClickListener

                if (isPlaying) {
                    videoView.pause()
                    isPlaying = false
                } else {
                    videoView.start()
                    isPlaying = true
                }

                // Unmute on first tap
                if (isMuted) {
                    mp.setVolume(1f, 1f)
                    isMuted = false
                }
            }

        } else {
            // Show ImageView fullscreen
            imageView = ImageView(this).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                scaleType = ImageView.ScaleType.FIT_CENTER
                setBackgroundColor(Color.BLACK)
            }
            setContentView(imageView)

            Glide.with(this)
                .load(uri)
                .into(imageView)

            imageView.setOnClickListener {
                finish() // Tap to exit fullscreen for image
            }
        }
    }

    private fun toggleMute() {
        val mp = videoView.mediaPlayer ?: return
        if (isMuted) {
            mp.setVolume(1f, 1f)
            isMuted = false
        } else {
            mp.setVolume(0f, 0f)
            isMuted = true
        }
    }

    // Extension to access MediaPlayer inside VideoView (hacky but works)
    private val VideoView.mediaPlayer: MediaPlayer?
        get() {
            return try {
                val field = VideoView::class.java.getDeclaredField("mMediaPlayer")
                field.isAccessible = true
                field.get(this) as? MediaPlayer
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
}
