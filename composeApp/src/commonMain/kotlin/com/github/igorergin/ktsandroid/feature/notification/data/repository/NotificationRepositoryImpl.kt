package com.github.igorergin.ktsandroid.feature.notification.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.github.igorergin.ktsandroid.core.notification.NotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NotificationRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : NotificationRepository {

    override fun getFcmToken(): Flow<String?> = dataStore.data.map { preferences ->
        preferences[FCM_TOKEN_KEY]
    }

    override suspend fun saveFcmToken(token: String) {
        dataStore.edit { preferences ->
            preferences[FCM_TOKEN_KEY] = token
        }
    }

    companion object {
        private val FCM_TOKEN_KEY = stringPreferencesKey("fcm_token")
    }
}
