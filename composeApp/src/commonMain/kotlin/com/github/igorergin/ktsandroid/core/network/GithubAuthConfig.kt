package com.github.igorergin.ktsandroid.core.network

import сom.github.igorergin.ktsandroid.AppConfig

/**
 * Конфигурация OAuth для GitHub.
 * Данные CLIENT_ID и CLIENT_SECRET безопасно подтягиваются из local.properties через AppConfig.
 */
object GithubAuthConfig {
    val CLIENT_ID = AppConfig.GITHUB_CLIENT_ID
    val CLIENT_SECRET = AppConfig.GITHUB_CLIENT_SECRET

    const val REDIRECT_URI = "github-explorer://callback"

    const val AUTH_ENDPOINT = "https://github.com/login/oauth/authorize"
    const val TOKEN_ENDPOINT = "https://github.com/login/oauth/access_token"
    const val SCOPES = "user,repo,gist"
}