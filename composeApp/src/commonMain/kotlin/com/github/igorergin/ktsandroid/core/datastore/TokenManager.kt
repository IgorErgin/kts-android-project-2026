package com.github.igorergin.ktsandroid.core.datastore

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class TokenManager(private val secureStorage: SecureStorage) {

    private val _accessToken = MutableStateFlow<String?>(null)
    val accessToken: StateFlow<String?> = _accessToken.asStateFlow()

    suspend fun initToken() = withContext(Dispatchers.IO) {
        secureStorage.getToken(KEY_ACCESS_TOKEN).onSuccess { token ->
            _accessToken.value = token
        }
    }

    suspend fun saveTokens(accessToken: String, refreshToken: String?): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                secureStorage.saveToken(KEY_ACCESS_TOKEN, accessToken).getOrThrow()
                if (refreshToken != null) {
                    secureStorage.saveToken(KEY_REFRESH_TOKEN, refreshToken).getOrThrow()
                }
                _accessToken.value = accessToken
            }
        }

    suspend fun getRefreshToken(): String? = withContext(Dispatchers.IO) {
        secureStorage.getToken(KEY_REFRESH_TOKEN).getOrNull()
    }

    fun isLoggedIn(): Boolean = _accessToken.value != null

    suspend fun clear(): Result<Unit> = withContext(Dispatchers.IO) {
        val result = secureStorage.clearTokens()
        if (result.isSuccess) {
            _accessToken.value = null
        }
        result
    }

    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
    }
}