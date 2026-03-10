package com.github.igorergin.ktsandroid.feature.auth.presentation

/**
 * Состояние экрана логина (стейт).
 */
data class LoginUiState(
    val login: String = "",
    val password: String = "",
    val isLoginButtonActive: Boolean = false,
    val error: String? = null,
    val isLoading: Boolean = false
)