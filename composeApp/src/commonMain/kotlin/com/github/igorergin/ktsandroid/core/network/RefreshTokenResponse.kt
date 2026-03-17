package com.github.igorergin.ktsandroid.core.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// DTO для ответа при обновлении токена
@Serializable
data class RefreshTokenResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String? = null
)