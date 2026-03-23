package com.github.igorergin.ktsandroid.core.network.auth

import com.github.igorergin.ktsandroid.core.datastore.TokenManager
import com.github.igorergin.ktsandroid.feature.auth.data.api.AuthApi
import io.github.aakira.napier.Napier
import io.ktor.client.plugins.auth.AuthConfig
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer

fun AuthConfig.installGithubAuth(tokenManager: TokenManager, authApi: AuthApi) {
    bearer {
        loadTokens {
            val access = tokenManager.accessToken.value
            val refresh = tokenManager.getRefreshToken()
            if (access != null) BearerTokens(access, refresh ?: "") else null
        }

        refreshTokens {
            val currentRefreshToken = tokenManager.getRefreshToken() ?: run {
                Napier.e("No refresh token available.", tag = "AuthPlugin")
                tokenManager.clear()
                return@refreshTokens null
            }

            try {
                val response = authApi.refreshToken(currentRefreshToken)
                val newAccess = response.accessToken
                val newRefresh = response.refreshToken ?: currentRefreshToken

                tokenManager.saveTokens(newAccess, newRefresh)
                BearerTokens(newAccess, newRefresh)
            } catch (e: Exception) {
                Napier.e("Failed to refresh token: ${e.message}", tag = "AuthPlugin")
                tokenManager.clear()
                null
            }
        }
    }
}