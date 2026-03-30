package com.github.igorergin.ktsandroid

import com.github.igorergin.ktsandroid.core.database.DatabaseFactory
import com.github.igorergin.ktsandroid.core.datastore.AndroidSecureStorage
import com.github.igorergin.ktsandroid.core.datastore.SecureStorage
import com.github.igorergin.ktsandroid.core.datastore.createAndroidDataStore
import com.github.igorergin.ktsandroid.core.util.AndroidAuthManager
import com.github.igorergin.ktsandroid.core.util.AndroidShareManager
import com.github.igorergin.ktsandroid.core.util.ShareManager
import com.github.igorergin.ktsandroid.feature.auth.domain.AuthManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.bind
import org.koin.dsl.module

val androidPlatformModule = module {
    single {
        createAndroidDataStore(
            context = androidContext(),
            fileName = "app_settings"
        )
    }

    single { AndroidSecureStorage(get()) } bind SecureStorage::class

    single { AndroidAuthManager(androidContext()) } bind AuthManager::class

    single { AndroidShareManager(androidContext()) } bind ShareManager::class

    single { DatabaseFactory(androidContext()) }
}