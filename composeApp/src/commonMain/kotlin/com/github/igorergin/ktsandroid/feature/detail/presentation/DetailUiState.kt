package com.github.igorergin.ktsandroid.feature.detail.presentation

import com.github.igorergin.ktsandroid.feature.repositories.data.network.GithubRepositoryDto

data class DetailUiState(
    val repository: GithubRepositoryDto? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)