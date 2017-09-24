package com.example.uberv.itunestopcharts.api.models

data class EntryResponse(
        val artistUrl: String,
        val artistId: Long,
        val artistName: String,
        val artworkUrl: String,
        val collectionName: String,
        val copyright: String,
        val genres: List<GenreResponse>,
        val id: Long,
        val kind: String,
        val name: String,
        val releaseDate: String,
        val url: String)