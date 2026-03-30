package com.github.igorergin.ktsandroid

import android.app.Application
import com.github.igorergin.ktsandroid.core.logging.CrashlyticsAntilog
import com.github.igorergin.ktsandroid.di.appModules
import io.github.aakira.napier.Napier
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Napier.base(CrashlyticsAntilog())

        startKoin {
            androidContext(this@MainApplication)

            androidLogger(Level.NONE)

            modules(appModules() + androidPlatformModule)
        }
    }
}