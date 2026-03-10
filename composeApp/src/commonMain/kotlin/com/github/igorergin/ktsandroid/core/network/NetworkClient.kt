package com.github.igorergin.ktsandroid.core.network

import com.github.igorergin.ktsandroid.core.datastore.TokenStorage
import io.github.aakira.napier.Napier
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object NetworkClient {
    val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
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
                    Napier.v(message, null, "Ktor-Network")
                }
            }
            level = LogLevel.INFO
        }
    }

    // Отдельный клиент для OAuth (без базового URL api.github.com)
    val oauthClient = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) { Napier.v(message, null, "Ktor-OAuth") }
            }
            level = LogLevel.INFO
        }
    }
}