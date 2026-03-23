package com.github.igorergin.ktsandroid.core.datastore

import kotlinx.coroutines.flow.Flow

actual class AppSettings {
    actual val isFirstLaunchFlow: Flow<Boolean>
        get() = TODO("Not yet implemented")

    actual suspend fun setFirstLaunch(isFirst: Boolean) {
    }
}