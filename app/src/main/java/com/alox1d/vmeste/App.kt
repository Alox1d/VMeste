package com.alox1d.vmeste

import android.app.Application
import com.yandex.mapkit.MapKitFactory

class App : Application() {
    init {
        instance = this
    }
    override fun onCreate() {
        super.onCreate()
        MapKitFactory.setApiKey("2eac0ecc-f3d0-4ad1-a35c-28f85bb27c96");
    }
    companion object {
        lateinit var instance: App

    }
}