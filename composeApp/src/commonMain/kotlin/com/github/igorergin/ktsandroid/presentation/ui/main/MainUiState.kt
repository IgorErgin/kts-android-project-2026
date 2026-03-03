package com.github.igorergin.ktsandroid.presentation.ui.main

import com.github.igorergin.ktsandroid.domain.model.UserProfile

// Состояние экрана профиля
data class MainUiState(
    val profile: UserProfile? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)