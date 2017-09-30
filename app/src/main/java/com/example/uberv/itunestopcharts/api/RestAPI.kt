package com.example.uberv.itunestopcharts.api

import com.example.uberv.itunestopcharts.BuildConfig
import com.example.uberv.itunestopcharts.api.models.Feed
import com.example.uberv.itunestopcharts.api.models.FeedDeserializer
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.DateFormat

class RestAPI {
    private val iTunesApi: ITunesApi


    init {
        val client = OkHttpClient().newBuilder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
                })
                .build()
        val gson = GsonBuilder()
                .setLenient()
                .registerTypeAdapter(
                        Feed::class.java, FeedDeserializer()
                )
                .setDateFormat(DateFormat.FULL, DateFormat.FULL)
                .create()
        val retrofit = Retrofit.Builder()
                .client(client)
                .baseUrl("http://ax.itunes.apple.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        iTunesApi = retrofit.create(ITunesApi::class.java)
    }

    fun getHotTracks(): Call<Feed> {
        return iTunesApi.getTopTen()
    }
}