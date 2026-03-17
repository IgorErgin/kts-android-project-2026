package com.github.igorergin.ktsandroid.core.network

import com.github.igorergin.ktsandroid.core.datastore.TokenStorage
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object NetworkClient {

    private val appJson = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
        isLenient = true
    }
    val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(appJson)
        }

        install(Auth) {
            bearer {
                loadTokens {
                    TokenStorage.getToken()?.let { BearerTokens(it, "") }
                }
            }
        }

        defaultRequest {
            url("https://api.github.com/")
            header("Accept", "application/vnd.github.v3+json")
        }

        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    Napier.v(message = message, tag = "Ktor-Network")
                }
            }
            level = LogLevel.INFO
        }
    }

    // Отдельный клиент для OAuth (без базового URL api.github.com)
    val oauthClient = HttpClient {
        install(ContentNegotiation) {
            json(appJson)
        }
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    Napier.v(message = message, tag = "Ktor-OAuth")
                }
            }
            level = LogLevel.INFO
        }
    }
}