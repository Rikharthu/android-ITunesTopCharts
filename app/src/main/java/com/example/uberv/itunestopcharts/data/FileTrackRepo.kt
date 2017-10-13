package com.example.uberv.itunestopcharts.data

import android.content.Context
import com.example.uberv.itunestopcharts.data.models.Feed
import com.google.gson.Gson
import java.io.File

private val FEED_FILE_NAME = "itunes_feed.json"

class FileTrackRepo(context: Context) : AbstractTrackRepo(context) {

    override fun saveFeed(feed: Feed) {
        val feedFile = getFeedFile()
        //TODO provide Gson with dagger instead
        val feedJson = Gson().toJson(feed)
        feedFile.writeText(feedJson)
    }

    private fun getFeedFile(): File {
        return File(context.filesDir, FEED_FILE_NAME)
    }

    override fun getFeed(): Feed? {
        val feedFile = getFeedFile()
        if (feedFile.exists()) {
            return Gson().fromJson(feedFile.readText(), Feed::class.java)
        }
        return null
    }
}