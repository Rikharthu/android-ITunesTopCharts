package com.example.uberv.itunestopcharts.api.models

data class Link(var rel: String? = null,
                var type: String? = null,
                var href: String? = null,
                var title: String? = null,
                var assetType: String? = null,
                var duration: Long? = null)