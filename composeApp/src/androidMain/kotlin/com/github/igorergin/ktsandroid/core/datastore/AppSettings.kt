package com.github.igorergin.ktsandroid.core.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

actual class AppSettings(private val context: Context) {

    companion object {
        private val IS_FIRST_LAUNCH = booleanPreferencesKey("is_first_launch")
    }

    actual val isFirstLaunchFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_FIRST_LAUNCH] ?: true
    }

    actual suspend fun setFirstLaunch(isFirst: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_FIRST_LAUNCH] = isFirst
        }
    }
}