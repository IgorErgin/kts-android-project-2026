package com.github.igorergin.ktsandroid.core.datastore

import kotlinx.coroutines.flow.Flow


expect class AppSettings {
    val isFirstLaunchFlow: Flow<Boolean>

    suspend fun setFirstLaunch(isFirst: Boolean)
}