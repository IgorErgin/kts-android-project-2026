package com.github.igorergin.ktsandroid.feature.auth.data.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OAuthTokenResponse(
    // Поля успешного ответа делаем nullable
    @SerialName("access_token") val accessToken: String? = null,
    @SerialName("token_type") val tokenType: String? = null,
    @SerialName("scope") val scope: String? = null,

    @SerialName("error") val error: String? = null,
    @SerialName("error_description") val errorDescription: String? = null
)