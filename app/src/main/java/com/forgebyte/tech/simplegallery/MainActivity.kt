package com.forgebyte.tech.simplegallery

import android.Manifest
import android.content.ContentUris
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val mediaList = mutableListOf<MediaItem>()

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value }) {
            loadMedia()
        } else {
            Toast.makeText(this, "Permissions required.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.mediaRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 3)

        checkPermissions()
    }

    private fun checkPermissions() {
        val permissions = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(android.Manifest.permission.READ_MEDIA_IMAGES)
            permissions.add(android.Manifest.permission.READ_MEDIA_VIDEO)
        } else {
            permissions.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        permissionLauncher.launch(permissions.toTypedArray())
    }

    private fun loadMedia() {
        val imageCursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null, null, null,
            "${MediaStore.Images.Media.DATE_ADDED} DESC"
        )
        val videoCursor = contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            null, null, null,
            "${MediaStore.Video.Media.DATE_ADDED} DESC"
        )

        imageCursor?.use {
            val uriIdx = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            while (it.moveToNext()) {
                val id = it.getLong(uriIdx)
                val uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                mediaList.add(MediaItem(uri, false))
            }
        }

        videoCursor?.use {
            val uriIdx = it.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            while (it.moveToNext()) {
                val id = it.getLong(uriIdx)
                val uri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id)
                mediaList.add(MediaItem(uri, true))
            }
        }

        mediaList.sortByDescending { it.uri.lastPathSegment }
        recyclerView.adapter = MediaAdapter(mediaList) { mediaItem ->
            val intent = if (mediaItem.isVideo) {
                Intent(this, FullscreenVideoActivity::class.java).apply {
                    putExtra(FullscreenMediaActivity.EXTRA_URI, mediaItem.uri)
                }
            } else {
                Intent(this, FullscreenMediaActivity::class.java).apply {
                    putExtra(FullscreenMediaActivity.EXTRA_URI, mediaItem.uri)
                    putExtra(FullscreenMediaActivity.EXTRA_IS_VIDEO, false)
                }
            }
            startActivity(intent)
        }

        }
    }