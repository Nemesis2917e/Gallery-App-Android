package com.forgebyte.tech.simplegallery

import android.net.Uri

data class MediaItem(
    val uri: Uri,
    val isVideo: Boolean
)
