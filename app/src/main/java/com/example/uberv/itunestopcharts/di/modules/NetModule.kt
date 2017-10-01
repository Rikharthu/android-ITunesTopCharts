package com.example.uberv.itunestopcharts.di.modules

import android.content.Context
import com.example.uberv.itunestopcharts.api.ITunesApiService
import com.example.uberv.itunestopcharts.api.models.Feed
import com.example.uberv.itunestopcharts.api.models.FeedDeserializer
import com.example.uberv.itunestopcharts.di.annotations.ApplicationContext
import com.example.uberv.itunestopcharts.utils.LiveDataCallAdapterFactory
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.text.DateFormat
import javax.inject.Named
import javax.inject.Singleton


@Module
class NetModule(private val baseUrl: String) {


    @Singleton
    @Provides
    fun provideITunesApiService(retrofit: Retrofit): ITunesApiService {
        return retrofit.create(ITunesApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideOkHttpCache(@ApplicationContext context: Context): Cache {
        Timber.d("providing OkHttpCache")
        val cacheSize = 10 * 1024 * 1024 // 10 MiB
        val cache = Cache(context.getCacheDir(), cacheSize.toLong())
        return cache
    }

    @Provides
    @Named("cached")
    @Singleton
    fun provideCachedOkHttpClient(cache: Cache): OkHttpClient {
        Timber.d("providing OkHttpClient (with cache)")
        return OkHttpClient.Builder().cache(cache).build()
    }

    @Provides
    @Named("non_cached")
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        Timber.d("providing OkHttpClient")
        return OkHttpClient()
    }

    @Provides
    @Singleton
    fun provideRetrofit(gson: Gson, @Named("cached") okHttpClient: OkHttpClient): Retrofit {
        Timber.d("providing Retrofit")
        val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(LiveDataCallAdapterFactory())
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .build()
        return retrofit
    }

    @Provides
    @Singleton
    fun provideGson() = GsonBuilder()
            .setLenient()
            .registerTypeAdapter(
                    Feed::class.java, FeedDeserializer()
            )
            .setDateFormat(DateFormat.FULL, DateFormat.FULL)
            .create()
}