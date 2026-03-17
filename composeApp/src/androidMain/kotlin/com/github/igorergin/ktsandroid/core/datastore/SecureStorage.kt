package com.github.igorergin.ktsandroid.core.datastore

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Android-реализация защищенного хранилища на базе Android Keystore System.
 */
actual class SecureStorage(context: Context) {

    private val sharedPreferences = context.getSharedPreferences("secure_prefs", Context.MODE_PRIVATE)
    private val keyAlias = "kts_secure_key_alias"
    private val provider = "AndroidKeyStore"
    private val transformation = "AES/GCM/NoPadding"

    private fun getSecretKey(): SecretKey {
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

    actual fun saveToken(key: String, value: String) {
        try {
            val cipher = Cipher.getInstance(transformation)
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey())

            val encryptedData = cipher.doFinal(value.toByteArray(Charsets.UTF_8))
            val iv = cipher.iv

            val combined = Base64.encodeToString(iv + encryptedData, Base64.DEFAULT)
            sharedPreferences.edit().putString(key, combined).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    actual fun getToken(key: String): String? {
        val encodedData = sharedPreferences.getString(key, null) ?: return null
        return try {
            val combined = Base64.decode(encodedData, Base64.DEFAULT)
            if (combined.size < 12) return null

            val iv = combined.copyOfRange(0, 12)
            val encryptedData = combined.copyOfRange(12, combined.size)

            val cipher = Cipher.getInstance(transformation)
            val spec = GCMParameterSpec(128, iv)
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), spec)

            String(cipher.doFinal(encryptedData), Charsets.UTF_8)
        } catch (e: Exception) {
            null
        }
    }

    actual fun clearTokens() {
        sharedPreferences.edit().clear().apply()
    }
}