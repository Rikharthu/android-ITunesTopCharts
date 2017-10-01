package com.example.uberv.itunestopcharts.di.modules

import android.app.Application
import android.content.Context
import com.example.uberv.itunestopcharts.di.annotations.ApplicationContext
import dagger.Module
import dagger.Provides
import timber.log.Timber
import javax.inject.Singleton


@Module
class AppModule(private val application: Application) {
    @Provides
    @Singleton
    fun provideApplication(): Application {
        Timber.d("providing Application")
        return application
    }

    @Provides
    @Singleton
    @ApplicationContext
    fun provideAppContext(): Context {
        Timber.d("providing Context (Application)")
        return application
    }
}