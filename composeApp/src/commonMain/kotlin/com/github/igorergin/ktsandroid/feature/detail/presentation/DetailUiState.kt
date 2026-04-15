package com.github.igorergin.ktsandroid.feature.detail.presentation

import androidx.compose.runtime.Immutable
import com.github.igorergin.ktsandroid.core.domain.error.AppError
import com.github.igorergin.ktsandroid.feature.detail.domain.model.GithubContent
import com.github.igorergin.ktsandroid.feature.detail.domain.model.PullRequest
import com.github.igorergin.ktsandroid.feature.detail.domain.model.RepositoryDetail

@Immutable
data class DetailUiState(
    val repository: RepositoryDetail? = null,
    val isFavorite: Boolean = false,
    val readme: String? = null,
    val isLoading: Boolean = false,
    val isReadmeLoading: Boolean = false,
    val error: AppError? = null,

    val isIssueDialogVisible: Boolean = false,
    val isIssueSending: Boolean = false,
    val issueError: AppError? = null,

    val contents: List<GithubContent> = emptyList(),
    val isContentsLoading: Boolean = false,
    val contentsError: AppError? = null,

    val isUploadDialogVisible: Boolean = false,
    val isUploading: Boolean = false,
    val uploadError: AppError? = null,

    val currentPath: String = "",
    val selectedFile: GithubContent.File? = null,
    val pullRequests: List<PullRequest> = emptyList(),
    val isPullsLoading: Boolean = false,
    val pullsError: AppError? = null,
    val selectedPullRequest: PullRequest? = null,
    val selectedTabIndex: Int = 0
)
