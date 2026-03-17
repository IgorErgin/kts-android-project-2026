package com.github.igorergin.ktsandroid.feature.repositories.presentation

import com.github.igorergin.ktsandroid.feature.repositories.domain.model.GithubRepository

data class MainUiState(
    val query: String = "",
    val repositories: List<GithubRepository> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isPaginating: Boolean = false,
    val error: String? = null,
    val page: Int = 1
)