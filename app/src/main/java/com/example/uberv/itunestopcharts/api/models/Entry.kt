package com.example.uberv.itunestopcharts.api.models

data class Entry(
        var name: String? = null,
        var image: List<Image>? = null,
        var link: List<Link>? = null,
        var collection: Collection? = null,
        var price: String? = null,
        var priceAmount: Double? = null,
        var priceCurrency: String? = null,
        var contentType: ContentType? = null,
        var rights: String? = null,
        var title: String? = null,
        var idLabel: String? = null, // TODO maybe url?
        var id: Long = 0,
        var artist: String? = null,
        var artistUrl: String? = null,
        var releaseDate: String? = null,
        var releaseDateLabel: String? = null
)