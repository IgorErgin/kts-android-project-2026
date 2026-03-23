package com.github.igorergin.ktsandroid.core.network

import com.github.igorergin.ktsandroid.core.datastore.TokenManager
import com.github.igorergin.ktsandroid.core.network.auth.installGithubAuth
import com.github.igorergin.ktsandroid.feature.auth.data.api.AuthApi
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object NetworkClient {

    val jsonConfig = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
        isLenient = true
        explicitNulls = false
    }

    val unauthenticatedClient by lazy {
        HttpClient {
            install(ContentNegotiation) { json(jsonConfig) }
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Napier.v(message, tag = "KtorAuthApi")
                    }
                }
                level = LogLevel.INFO
            }
        }
    }

    fun createHttpClient(tokenManager: TokenManager, authApi: AuthApi): HttpClient {
        return HttpClient {
            install(ContentNegotiation) { json(jsonConfig) }

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
            }

            install(Auth) {
                installGithubAuth(tokenManager, authApi)
            }
        }
    }
}