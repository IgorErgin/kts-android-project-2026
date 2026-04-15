package com.github.igorergin.ktsandroid.feature.detail.presentation

import com.github.igorergin.ktsandroid.feature.detail.domain.model.RepositoryDetail

data class DetailUiState(
    val repository: RepositoryDetail? = null,
    val readme: String? = null,
    val isLoading: Boolean = false,
    val isReadmeLoading: Boolean = false,
    val error: String? = null,

    val isIssueDialogVisible: Boolean = false,
    val isIssueSending: Boolean = false,
    val issueError: String? = null
)