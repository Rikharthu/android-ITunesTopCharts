package com.example.uberv.itunestopcharts.data

import com.example.uberv.itunestopcharts.data.models.Feed

interface TrackRepo {
    fun getFeed(): Feed?
    fun saveFeed(feed: Feed)
}