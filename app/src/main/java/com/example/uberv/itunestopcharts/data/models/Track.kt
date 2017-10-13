package com.example.uberv.itunestopcharts.data.models

import com.example.uberv.itunestopcharts.api.models.Entry
import com.example.uberv.itunestopcharts.api.models.Image
import com.example.uberv.itunestopcharts.api.models.Link
import java.text.SimpleDateFormat
import java.util.*

data class Track(
        var name: String? = null,
        val imageUrl: String? = null,
        val album: String? = null,
        val price: String? = null,
        val priceAmount: Float? = null,
        val currency: String? = null,
        val rights: String? = null,
        val title: String? = null,
        val previewUrl: String? = null,
        val duration: Long? = null,
        val artist: String? = null,
        val category: String? = null,
        val releaseDate: Date? = null // TODO or use String or use DateTime?
)

// TODO maybe move it to some kind init block?
private val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")

fun Entry.asTrack(): Track? {
    val images = this.image
    val imageUrl: String?
    if (images != null && images.isNotEmpty()) {
        imageUrl = chooseOptimalImage(images)
    } else {
        imageUrl = null
    }
    var previewUrl: String? = null
    var duration: Long? = null
    if (this.link != null) {
        val link = extractPreviewLink(this.link!!)
        previewUrl = link?.href
        duration = link?.duration
    }

//    val releaseDate =Instant.parse(this.releaseDate) // API 26
    var releaseDate: Date? = null
    if (this.releaseDate != null) {
        releaseDate = sdf.parse(this.releaseDate)
    }

    return Track(name = this.name, imageUrl = imageUrl, album = this.collection?.name,
            price = this.price, priceAmount = this.priceAmount, currency = this.priceCurrency,
            rights = this.rights, title = this.title, previewUrl = previewUrl,
            duration = duration, artist = this.artist, releaseDate = releaseDate)
}

private fun extractPreviewLink(links: List<Link>): Link? {
    return links
            .firstOrNull { "preview".equals(it.title, true) }
}

private fun chooseOptimalImage(images: List<Image>): String? {
    var result: String? = null
    var maxHeight = 0
    for (image in images) {
        if (image.height != null && image.height > maxHeight) {
            maxHeight = image.height
            result = image.url
        }
    }
    return result
}
