package com.github.igorergin.ktsandroid.feature.repositories.presentation

import androidx.compose.runtime.Immutable
import com.github.igorergin.ktsandroid.core.domain.error.AppError
import com.github.igorergin.ktsandroid.feature.repositories.domain.model.GithubRepository

@Immutable
data class MainUiState(
    val query: String = "",
    val repositories: List<GithubRepository> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isPaginating: Boolean = false,
    val error: AppError? = null,
    val page: Int = 1,
    val isOfflineData: Boolean = false
)
