package com.github.igorergin.ktsandroid.feature.profile.presentation

import androidx.compose.runtime.Immutable
import com.github.igorergin.ktsandroid.core.domain.error.AppError
import com.github.igorergin.ktsandroid.feature.profile.domain.model.GithubEvent
import com.github.igorergin.ktsandroid.feature.profile.domain.model.UserProfile
import com.github.igorergin.ktsandroid.feature.repositories.domain.model.GithubRepository

@Immutable
data class ProfileUiState(
    val profile: UserProfile? = null,
    val repos: List<GithubRepository> = emptyList(),
    val events: List<GithubEvent> = emptyList(),
    val selectedTab: Int = 0,
    val isLoading: Boolean = true,
    val isActivityLoading: Boolean = false,
    val error: AppError? = null
)
