package com.example.uberv.itunestopcharts.api

import android.arch.lifecycle.LiveData
import com.example.uberv.itunestopcharts.api.models.ApiResponse
import com.example.uberv.itunestopcharts.api.models.Feed
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ITunesApiService {
    @GET("/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=50/json")
    fun getTopTen(): Call<Feed>

    @GET("/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit={limit}/json")
    fun getTopTracks(@Path("limit") limit: Int):LiveData<ApiResponse<Feed>>
}