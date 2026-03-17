package com.github.igorergin.ktsandroid.feature.auth.data.repository

import com.github.igorergin.ktsandroid.core.datastore.TokenManager
import com.github.igorergin.ktsandroid.core.network.GithubAuthConfig
import com.github.igorergin.ktsandroid.feature.auth.data.network.OAuthTokenResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.CancellationException

class GithubAuthRepository(
    private val authHttpClient: HttpClient,
    private val tokenManager: TokenManager
) {

    suspend fun exchangeCodeForToken(code: String): Result<String> {
        return try {
            val response: OAuthTokenResponse = authHttpClient.post(GithubAuthConfig.TOKEN_ENDPOINT) {
                header(HttpHeaders.Accept, "application/json")
                url {
                    parameters.append("client_id", GithubAuthConfig.CLIENT_ID)
                    parameters.append("client_secret", GithubAuthConfig.CLIENT_SECRET)
                    parameters.append("code", code)
                    parameters.append("redirect_uri", GithubAuthConfig.REDIRECT_URI)
                }
            }.body()

            if (response.error != null) {
                return Result.failure(
                    Exception(response.errorDescription ?: response.error)
                )
            }

            if (response.accessToken != null) {
                tokenManager.saveTokens(
                    accessToken = response.accessToken,
                    refreshToken = response.refreshToken
                )
                Result.success(response.accessToken)
            } else {
                Result.failure(Exception("Токен не был получен из ответа GitHub"))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}