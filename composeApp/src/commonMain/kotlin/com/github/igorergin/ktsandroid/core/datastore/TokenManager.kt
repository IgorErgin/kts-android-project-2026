package com.github.igorergin.ktsandroid.core.datastore

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


expect class SecureStorage {
    fun saveToken(key: String, value: String)
    fun getToken(key: String): String?
    fun clearTokens()
}

class TokenManager(private val secureStorage: SecureStorage) {

    private val _accessToken = MutableStateFlow<String?>(secureStorage.getToken(KEY_ACCESS_TOKEN))
    val accessToken: StateFlow<String?> = _accessToken.asStateFlow()

    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
    }

    fun saveTokens(accessToken: String, refreshToken: String?) {
        secureStorage.saveToken(KEY_ACCESS_TOKEN, accessToken)
        if (refreshToken != null) {
            secureStorage.saveToken(KEY_REFRESH_TOKEN, refreshToken)
        }
        _accessToken.value = accessToken
    }

    fun getRefreshToken(): String? = secureStorage.getToken(KEY_REFRESH_TOKEN)

    fun isLoggedIn(): Boolean = _accessToken.value != null

    fun clear() {
        secureStorage.clearTokens()
        _accessToken.value = null
    }
}