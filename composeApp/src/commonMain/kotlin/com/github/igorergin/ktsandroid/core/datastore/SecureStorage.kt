package com.github.igorergin.ktsandroid.core.datastore


interface SecureStorage {
    suspend fun saveToken(key: String, value: String): Result<Unit>
    suspend fun getToken(key: String): Result<String?>
    suspend fun clearTokens(): Result<Unit>
}