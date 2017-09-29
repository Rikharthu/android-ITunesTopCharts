package com.example.uberv.itunestopcharts.api.models

data class Feed(
        val author: String? = null,
        val authorUrl: String? = null,
        val entry: List<Entry>? = null,
        val updated: String? = null,
        val rights: String? = null,
        val title: String? = null,
        val icon: String? = null,
        val link: List<Link>? = null,
        val id: String? = null
)