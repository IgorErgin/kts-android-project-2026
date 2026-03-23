package com.github.igorergin.ktsandroid.feature.auth.data.api

import com.github.igorergin.ktsandroid.core.network.RefreshTokenResponse
import com.github.igorergin.ktsandroid.feature.auth.data.network.OAuthTokenResponse


interface AuthApi {
    suspend fun exchangeCodeForToken(code: String): OAuthTokenResponse
    suspend fun refreshToken(refreshToken: String): RefreshTokenResponse
}