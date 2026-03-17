package com.github.igorergin.ktsandroid.feature.auth.domain

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

