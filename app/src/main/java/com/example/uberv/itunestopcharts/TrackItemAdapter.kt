package com.example.uberv.itunestopcharts

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.uberv.itunestopcharts.api.models.Entry
import com.example.uberv.itunestopcharts.api.models.Image
import kotlinx.android.synthetic.main.music_item.view.*
import timber.log.Timber

class TrackItemAdapter(val items: List<Entry>, val listener: (Entry) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val ITEM_TYPE_HEADER = 1

    val ITEM_TYPE_TRACK = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == ITEM_TYPE_HEADER) {

        } else if (viewType == ITEM_TYPE_TRACK) {
            return TrackViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.music_item, parent, false))
        }
        throw RuntimeException("There is no type that matches the type $viewType, make sure your using types correctly")
    }

    override fun getItemCount(): Int = items.size + 1

    override fun getItemViewType(position: Int) = if (position == 0) ITEM_TYPE_HEADER else ITEM_TYPE_TRACK

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is TrackViewHolder) {
            val entry = items[position]
            holder.bind(entry)
            holder.itemView.setOnClickListener { listener(entry) }
            holder.pos.text = (position + 1).toString()
        } else if (holder is HeaderViewHolder) {

        } else {
            Timber.e("No instance of ViewHolder found")
        }
    }

    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.music_item_title
        val artist: TextView = itemView.music_item_artist
        val image: ImageView = itemView.music_item_image
        val pos: TextView = itemView.music_item_rating_top

        fun bind(entry: Entry) {
            title.text = entry.name
            artist.text = entry.artist
            val images = entry.image
            if (images != null && images.isNotEmpty()) {
                var img: Image? = null
                images.forEach {
                    if (img == null || img!!.height!! < it.height!!) {
                        img = it
                    }
                }
                Glide.with(image).load(img?.url).into(image)
            }
        }
    }
}