package com.github.igorergin.ktsandroid.feature.detail.presentation

import com.github.igorergin.ktsandroid.feature.detail.domain.model.RepositoryDetail

data class DetailUiState(
    val repository: RepositoryDetail? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)