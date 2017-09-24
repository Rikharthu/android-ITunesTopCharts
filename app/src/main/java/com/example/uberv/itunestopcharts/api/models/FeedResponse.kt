package com.example.uberv.itunestopcharts.api.models

data class FeedResponse(
        val title:String,
        val id:String,
        val author:AuthorResponse,
        val links:List<Map<String,String>>,
        val results: List<EntryResponse>,
        val copyright: String,
        val country: String,
        val icon: String,
        val updated: String
)