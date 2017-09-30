package com.example.uberv.itunestopcharts

import android.app.Application
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import com.example.uberv.itunestopcharts.utils.CrashlyticsTree
import com.example.uberv.itunestopcharts.utils.DevelopmentTree
import io.fabric.sdk.android.Fabric
import timber.log.Timber

class App : Application() {
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