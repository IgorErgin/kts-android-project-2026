package com.github.igorergin.ktsandroid.core.datastore

actual class SecureStorage {
    actual fun saveToken(key: String, value: String) {
    }

    actual fun getToken(key: String): String? {
        TODO("Not yet implemented")
    }

    actual fun clearTokens() {
    }
}