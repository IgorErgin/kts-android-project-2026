package com.github.igorergin.ktsandroid.feature.auth.data.repository

import com.github.igorergin.ktsandroid.core.network.GithubAuthConfig
import com.github.igorergin.ktsandroid.core.network.NetworkClient
import com.github.igorergin.ktsandroid.feature.auth.data.network.OAuthTokenResponse
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class GithubAuthRepository {
    private val client = NetworkClient.oauthClient

    suspend fun exchangeCodeForToken(code: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val response: OAuthTokenResponse = client.post(GithubAuthConfig.TOKEN_ENDPOINT) {
                    header(HttpHeaders.Accept, "application/json")
                    url {
                        parameters.append("client_id", GithubAuthConfig.CLIENT_ID)
                        parameters.append("client_secret", GithubAuthConfig.CLIENT_SECRET)
                        parameters.append("code", code)
                        parameters.append("redirect_uri", GithubAuthConfig.REDIRECT_URI)
                    }
                }.body()

                // Проверяем, вернул ли GitHub ошибку
                if (response.error != null) {
                    return@withContext Result.failure(
                        Exception(response.errorDescription ?: response.error)
                    )
                }

                // Если ошибки нет, проверяем токен
                if (response.accessToken != null) {
                    Result.success(response.accessToken)
                } else {
                    Result.failure(Exception("Токен не был получен из ответа GitHub"))
                }

            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}