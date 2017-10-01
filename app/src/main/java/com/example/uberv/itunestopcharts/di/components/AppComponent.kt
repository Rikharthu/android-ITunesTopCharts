package com.example.uberv.itunestopcharts.di.components

import com.example.uberv.itunestopcharts.MainActivity
import com.example.uberv.itunestopcharts.di.modules.AppModule
import com.example.uberv.itunestopcharts.di.modules.NetModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AppModule::class, NetModule::class))
interface AppComponent {
    fun inject(activity: MainActivity)
}