package com.github.igorergin.ktsandroid.core.datastore

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Хранилище токена авторизации.
 * Использует StateFlow для реактивного обновления состояния во всем приложении.
 */
object TokenStorage {
    private val _token = MutableStateFlow<String?>(null)
    val token: StateFlow<String?> = _token.asStateFlow()

    /**
     * Сохраняет полученный OAuth токен.
     */
    fun saveToken(newToken: String) {
        _token.value = newToken
    }

    /**
     * Очищает токен при выходе из системы.
     */
    fun clearToken() {
        _token.value = null
    }

    /**
     * Синхронное получение токена для сетевых запросов.
     */
    fun getToken(): String? = _token.value
}