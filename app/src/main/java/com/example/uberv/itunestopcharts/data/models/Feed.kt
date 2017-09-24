package com.example.uberv.itunestopcharts.data.models

import com.example.uberv.itunestopcharts.api.models.AuthorResponse
import com.example.uberv.itunestopcharts.api.models.EntryResponse

data class Feed(
        val title:String,
        val id:String,
        val author: AuthorResponse,
        val results: List<EntryResponse>,
        val copyright: String,
        val country: String,
        val icon: String,
        val updated: String
)