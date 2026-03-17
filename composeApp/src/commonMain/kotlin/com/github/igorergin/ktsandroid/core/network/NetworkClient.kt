package com.github.igorergin.ktsandroid.core.network

import com.github.igorergin.ktsandroid.core.datastore.TokenManager
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object NetworkClient {

    private val jsonConfig = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
        isLenient = true
        explicitNulls = false
    }


    fun createHttpClient(tokenManager: TokenManager): HttpClient {
        return HttpClient {
            install(ContentNegotiation) {
                json(jsonConfig)
            }

            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Napier.v(message, tag = "KtorNetwork")
                    }
                }
                level = LogLevel.INFO
            }

            defaultRequest {
                url {
                    protocol = URLProtocol.HTTPS
                    host = "api.github.com"
                }
                header("Accept", "application/vnd.github.v3+json")
                header("X-GitHub-Api-Version", "2022-11-28")
            }

            install(Auth) {
                bearer {
                    loadTokens {
                        val access = tokenManager.accessToken.value
                        val refresh = tokenManager.getRefreshToken()
                        if (access != null) {
                            BearerTokens(access, refresh ?: "")
                        } else {
                            null
                        }
                    }

                    refreshTokens {
                        val currentRefreshToken = tokenManager.getRefreshToken()

                        if (currentRefreshToken == null) {
                            Napier.e("No refresh token available to refresh session.", tag = "AuthPlugin")
                            tokenManager.clear()
                            return@refreshTokens null
                        }

                        try {
                            Napier.d("Attempting to refresh token...", tag = "AuthPlugin")


                            val refreshClient = HttpClient {
                                install(ContentNegotiation) { json(jsonConfig) }
                            }

                            val response: RefreshTokenResponse = refreshClient.post("https://github.com/login/oauth/access_token") {
                                contentType(ContentType.Application.Json)
                                header("Accept", "application/json")
                                setBody(mapOf(
                                    "client_id" to GithubAuthConfig.CLIENT_ID,
                                    "client_secret" to GithubAuthConfig.CLIENT_SECRET,
                                    "grant_type" to "refresh_token",
                                    "refresh_token" to currentRefreshToken
                                ))
                            }.body()

                            refreshClient.close()

                            Napier.d("Token refreshed successfully!", tag = "AuthPlugin")


                            tokenManager.saveTokens(
                                accessToken = response.accessToken,
                                refreshToken = response.refreshToken ?: currentRefreshToken
                            )

                            // Возвращаем новые токены для Ktor, чтобы он повторил исходный запрос
                            BearerTokens(response.accessToken, response.refreshToken ?: currentRefreshToken)

                        } catch (e: Exception) {
                            Napier.e("Failed to refresh token: ${e.message}", tag = "AuthPlugin")
                            // Пункт ДЗ: если обновить не получилось - форс логаут
                            tokenManager.clear()
                            null
                        }
                    }

                    // Посылать токены на все адреса (можно ограничить конкретным хостом api.github.com)
                    sendWithoutRequest { request ->
                        request.url.host == "api.github.com"
                    }
                }
            }
        }
    }
}