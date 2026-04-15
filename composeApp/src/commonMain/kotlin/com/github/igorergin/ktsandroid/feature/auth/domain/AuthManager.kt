package com.github.igorergin.ktsandroid.feature.auth.domain

import kotlinx.coroutines.flow.SharedFlow

expect class AuthManager {
    val authCodeFlow: SharedFlow<String>
    fun launchAuthFlow()
    fun dispose()
}
