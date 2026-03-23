package com.github.igorergin.ktsandroid.feature.auth.data.api

import com.github.igorergin.ktsandroid.core.network.GithubAuthConfig
import com.github.igorergin.ktsandroid.core.network.RefreshTokenResponse
import com.github.igorergin.ktsandroid.feature.auth.data.network.OAuthTokenResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType

class AuthApiImpl(
    private val unauthenticatedClient: HttpClient
) : AuthApi {

    override suspend fun exchangeCodeForToken(code: String): OAuthTokenResponse {
        return unauthenticatedClient.post(GithubAuthConfig.TOKEN_ENDPOINT) {
            header(HttpHeaders.Accept, "application/json")
            url {
                parameters.append("client_id", GithubAuthConfig.CLIENT_ID)
                parameters.append("client_secret", GithubAuthConfig.CLIENT_SECRET)
                parameters.append("code", code)
                parameters.append("redirect_uri", GithubAuthConfig.REDIRECT_URI)
            }
        }.body()
    }

    override suspend fun refreshToken(refreshToken: String): RefreshTokenResponse {
        return unauthenticatedClient.post(GithubAuthConfig.TOKEN_ENDPOINT) {
            contentType(ContentType.Application.Json)
            header(HttpHeaders.Accept, "application/json")
            setBody(
                mapOf(
                    "client_id" to GithubAuthConfig.CLIENT_ID,
                    "client_secret" to GithubAuthConfig.CLIENT_SECRET,
                    "grant_type" to "refresh_token",
                    "refresh_token" to refreshToken
                )
            )
        }.body()
    }
}