package com.example.uberv.itunestopcharts.api

import com.example.uberv.itunestopcharts.BuildConfig
import com.example.uberv.itunestopcharts.api.models.FeedResponseMain
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class RestAPI {
    private val iTunesApi: ITunesApi


    init {
        val client = OkHttpClient().newBuilder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
                })
                .build()
        val retrofit = Retrofit.Builder()
                .client(client)
                .baseUrl("https://rss.itunes.apple.com/api/v1/")
                .addConverterFactory(MoshiConverterFactory.create().asLenient())
                .build()
        iTunesApi = retrofit.create(ITunesApi::class.java)
    }

    public fun getHotTracks(): Call<FeedResponseMain> {
        return iTunesApi.getTopTen()
    }
}