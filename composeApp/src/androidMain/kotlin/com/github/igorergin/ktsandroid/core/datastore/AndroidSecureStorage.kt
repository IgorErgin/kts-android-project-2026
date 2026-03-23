package com.github.igorergin.ktsandroid.core.datastore

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class AndroidSecureStorage(
    private val dataStore: DataStore<Preferences>
) : SecureStorage {

    private val keyAlias = "kts_secure_key_v2"
    private val provider = "AndroidKeyStore"
    private val transformation = "AES/GCM/NoPadding"

    override suspend fun saveToken(key: String, value: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val cipher = Cipher.getInstance(transformation)
            cipher.init(Cipher.ENCRYPT_MODE, getOrCreateKey())

            val encryptedBytes = cipher.doFinal(value.encodeToByteArray())
            val iv = cipher.iv

            val combined = Base64.encodeToString(iv + encryptedBytes, Base64.NO_WRAP)

            dataStore.edit { prefs ->
                prefs[stringPreferencesKey(key)] = combined
            }
            Unit
        }
    }

    override suspend fun getToken(key: String): Result<String?> = withContext(Dispatchers.IO) {
        runCatching {
            val encryptedBase64 = dataStore.data
                .map { it[stringPreferencesKey(key)] }
                .firstOrNull() ?: return@runCatching null

            val combined = Base64.decode(encryptedBase64, Base64.NO_WRAP)

            val iv = combined.sliceArray(0 until 12)
            val encryptedBytes = combined.sliceArray(12 until combined.size)

            val cipher = Cipher.getInstance(transformation)
            val spec = GCMParameterSpec(128, iv)
            cipher.init(Cipher.DECRYPT_MODE, getOrCreateKey(), spec)

            cipher.doFinal(encryptedBytes).decodeToString()
        }
    }

    override suspend fun clearTokens(): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            dataStore.edit { it.clear() }
            Unit
        }
    }

    private fun getOrCreateKey(): SecretKey {
        val keyStore = KeyStore.getInstance(provider).apply { load(null) }
        keyStore.getKey(keyAlias, null)?.let { return it as SecretKey }

        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, provider)
        val spec = KeyGenParameterSpec.Builder(
            keyAlias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .build()

        keyGenerator.init(spec)
        return keyGenerator.generateKey()
    }
}