package com.forgebyte.tech.simplegallery

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

class FullscreenVideoActivity : AppCompatActivity() {

    private var player: ExoPlayer? = null
    private lateinit var playerView: PlayerView
    private lateinit var mediaUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_fullscreen_video)

        mediaUri = intent.getParcelableExtra("extra_uri") ?: run {
            finish()
            return
        }

        playerView = findViewById(R.id.playerView)
        initializePlayer()
    }


    private fun initializePlayer() {
        player = ExoPlayer.Builder(this).build()
        playerView.player = player

        val mediaItem = MediaItem.fromUri(mediaUri)
        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.playWhenReady = false // Start paused, user taps play
    }


    override fun onStop() {
        super.onStop()
        player?.release()
        player = null
    }
}
