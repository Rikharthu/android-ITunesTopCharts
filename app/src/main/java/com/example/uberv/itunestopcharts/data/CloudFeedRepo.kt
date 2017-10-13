package com.example.uberv.itunestopcharts.data

import android.content.Context
import com.example.uberv.itunestopcharts.data.models.Feed
import java.lang.IllegalStateException

class CloudFeedRepo(context: Context) : AbstractTrackRepo(context) {
    override fun getFeed(): Feed? {
        TODO("move all API logic here and provide different implementations of repos based on conditions via Dagger")
    }

    override fun saveFeed(feed: Feed) {
        throw IllegalStateException("${CloudFeedRepo::class.java.simpleName} cannot be used to safe feed")
    }
}