package com.example.uberv.itunestopcharts

import android.app.Activity
import android.app.Application
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import com.example.uberv.itunestopcharts.di.components.AppComponent
import com.example.uberv.itunestopcharts.di.components.DaggerAppComponent
import com.example.uberv.itunestopcharts.di.modules.AppModule
import com.example.uberv.itunestopcharts.di.modules.NetModule
import com.example.uberv.itunestopcharts.utils.CrashlyticsTree
import com.example.uberv.itunestopcharts.utils.DevelopmentTree
import io.fabric.sdk.android.Fabric
import timber.log.Timber

class App : Application() {

    val component: AppComponent by lazy {
        DaggerAppComponent
                .builder()
                .appModule(AppModule(this))
                .netModule(NetModule("http://ax.itunes.apple.com/"))
                .build()
    }

    override fun onCreate() {
        super.onCreate()
        val core: CrashlyticsCore = CrashlyticsCore.Builder()
                // TODO now enabled for debug purposes
//                .disabled(BuildConfig.DEBUG) // disable in debug
                .build()
        Fabric.with(this, Crashlytics.Builder().core(core).build())

        if (BuildConfig.DEBUG) {
            Timber.plant(DevelopmentTree())
        }
        Timber.plant(CrashlyticsTree())
    }
}