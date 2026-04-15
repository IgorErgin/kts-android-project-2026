package com.github.igorergin.ktsandroid.di

import com.github.igorergin.ktsandroid.core.database.DatabaseFactory
import com.github.igorergin.ktsandroid.core.datastore.AndroidSecureStorage
import com.github.igorergin.ktsandroid.core.datastore.AppSettings
import com.github.igorergin.ktsandroid.core.datastore.SecureStorage
import com.github.igorergin.ktsandroid.core.datastore.TokenManager
import com.github.igorergin.ktsandroid.core.datastore.createAndroidDataStore
import com.github.igorergin.ktsandroid.core.util.AndroidShareManager
import com.github.igorergin.ktsandroid.core.util.ShareManager
import com.github.igorergin.ktsandroid.feature.repositories.data.local.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.bind
import org.koin.dsl.module

val androidModule = module {
    single { DatabaseFactory(androidContext()).createBuilder().build() }

    single { get<AppDatabase>().repositoryDao() }

    single(org.koin.core.qualifier.named("settings_ds")) {
        createAndroidDataStore(androidContext(), "settings")
    }
    single(org.koin.core.qualifier.named("tokens_ds")) {
        createAndroidDataStore(androidContext(), "secure_tokens")
    }

    single { AppSettings(get(org.koin.core.qualifier.named("settings_ds"))) }
    single { AndroidSecureStorage(get(org.koin.core.qualifier.named("tokens_ds"))) } bind SecureStorage::class
    single { TokenManager(get()) }

    single { AndroidShareManager(androidContext()) } bind ShareManager::class
}


fun allAppModules() = appModules() + androidModule