package com.example.uberv.itunestopcharts

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import com.example.uberv.itunestopcharts.api.models.Feed
import com.example.uberv.itunestopcharts.api.models.FeedDeserializer
import com.example.uberv.itunestopcharts.utils.readFromAssets
import com.google.gson.GsonBuilder
import timber.log.Timber
import java.text.DateFormat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val feedJson = readFromAssets("topcharts.json", this)

        val gson = GsonBuilder()
                .setLenient()
                .registerTypeAdapter(
                        Feed::class.java, FeedDeserializer()
                )
                .setDateFormat(DateFormat.FULL, DateFormat.FULL)
                .create()
        val feed = gson.fromJson(feedJson, Feed::class.java)

        val textView = findViewById<TextView>(R.id.textview)
        textView.isSelected = true

        Timber.d(feedJson)
    }
}
