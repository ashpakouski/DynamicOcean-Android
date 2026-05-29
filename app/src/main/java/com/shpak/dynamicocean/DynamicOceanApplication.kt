package com.shpak.dynamicocean

import android.app.Application
import com.shpak.dynamicocean.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class DynamicOceanApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@DynamicOceanApplication)
            modules(appModule)
        }
    }
}
