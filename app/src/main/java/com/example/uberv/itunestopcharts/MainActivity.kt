package com.example.uberv.itunestopcharts

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.uberv.itunestopcharts.api.RestAPI
import com.example.uberv.itunestopcharts.api.models.FeedResponseMain
import com.example.uberv.itunestopcharts.data.asFeed
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val restApi = RestAPI()
        restApi.getHotTracks().enqueue(object : Callback<FeedResponseMain> {
            override fun onResponse(call: Call<FeedResponseMain>?, response: Response<FeedResponseMain>?) {
                var feed = response?.body()?.feed?.asFeed()
                Timber.d(feed.toString())
            }

            override fun onFailure(call: Call<FeedResponseMain>?, t: Throwable?) {
                TODO("not implemented")
            }

        })
    }
}
