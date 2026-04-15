package com.github.igorergin.ktsandroid.core.notification

import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    fun getFcmToken(): Flow<String?>
    suspend fun saveFcmToken(token: String)
}
