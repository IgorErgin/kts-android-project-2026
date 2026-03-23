package com.github.igorergin.ktsandroid.feature.profile.presentation

import com.github.igorergin.ktsandroid.feature.profile.domain.model.UserProfile

data class ProfileUiState(
    val profile: UserProfile? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)