package com.example.uberv.itunestopcharts.api

import com.example.uberv.itunestopcharts.api.models.Feed
import retrofit2.Call
import retrofit2.http.GET

interface ITunesApi {
    @GET("/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=10/json")
    fun getTopTen(): Call<Feed>
}