package com.github.igorergin.ktsandroid.feature.auth.domain

import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.flow.SharedFlow

/**
 * Кроссплатформенный интерфейс для запуска авторизации.
 */
interface AuthManager {
    val authCodeFlow: SharedFlow<String>
    fun launchAuthFlow()

    /**
     * Очистка ресурсов (остановка фоновых сервисов браузера)
     */
    fun dispose()
}

val LocalAuthManager = staticCompositionLocalOf<AuthManager> {
    error("AuthManager not provided! Make sure it's provided at the platform level in MainActivity.")
}