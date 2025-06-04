package com.forgebyte.tech.simplegallery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MediaAdapter(
private val items: List<MediaItem>,
private val onItemClick: (MediaItem) -> Unit
) : RecyclerView.Adapter<MediaAdapter.MediaViewHolder>() {

    inner class MediaViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_media, parent, false)
        return MediaViewHolder(view)
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        val item = items[position]
        val imageView = holder.view.findViewById<ImageView>(R.id.mediaThumbnail)
        val videoIcon = holder.view.findViewById<ImageView>(R.id.videoIcon)

        Glide.with(holder.view.context)
            .load(item.uri)
            .centerCrop()
            .into(imageView)

        videoIcon.visibility = if (item.isVideo) View.VISIBLE else View.GONE

        holder.view.setOnClickListener {
            onItemClick(item)
        }
    }


    override fun getItemCount() = items.size
}
