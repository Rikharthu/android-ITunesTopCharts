package com.example.uberv.itunestopcharts

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.uberv.itunestopcharts.api.models.Entry
import kotlinx.android.synthetic.main.music_item.view.*

class TrackItemAdapter(val items: List<Entry>, val listener: (Entry) -> Unit) : RecyclerView.Adapter<TrackItemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.music_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = items[position]
        holder.bind(entry)
        holder.itemView.setOnClickListener { listener(entry) }
        holder.pos.text = (position+1).toString()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val title: TextView = itemView.music_item_title
        val artist: TextView = itemView.music_item_artist
        val image: ImageView = itemView.music_item_image
        val pos: TextView = itemView.music_item_rating_top

        fun bind(entry: Entry) {
            title.text = entry.name
            artist.text = entry.artist
            val images = entry.image
            if (images != null && images.isNotEmpty()) {
                Glide.with(image).load(images.get(0).url).into(image)
            }
        }
    }
}