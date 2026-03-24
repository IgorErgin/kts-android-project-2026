package com.github.igorergin.ktsandroid.feature.detail.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.igorergin.ktsandroid.core.util.AppDispatchers
import com.github.igorergin.ktsandroid.core.util.ShareManager
import com.github.igorergin.ktsandroid.feature.detail.domain.usecase.CreateIssueUseCase
import com.github.igorergin.ktsandroid.feature.detail.domain.usecase.GetReadmeUseCase
import com.github.igorergin.ktsandroid.feature.detail.domain.usecase.GetRepositoryDetailsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetailViewModel(
    private val owner: String,
    private val repo: String,
    private val getRepositoryDetailsUseCase: GetRepositoryDetailsUseCase,
    private val getReadmeUseCase: GetReadmeUseCase,
    private val createIssueUseCase: CreateIssueUseCase,
    private val shareManager: ShareManager,
    private val dispatchers: AppDispatchers
) : ViewModel() {

    private val _state = MutableStateFlow(DetailUiState())
    val state = _state.asStateFlow()

    init {
        loadAll()
    }

    fun loadAll() {
        viewModelScope.launch(dispatchers.io) {
            _state.update { it.copy(isLoading = true, error = null) }

            getRepositoryDetailsUseCase(owner, repo).onSuccess { data ->
                _state.update { it.copy(isLoading = false, repository = data) }
                loadReadme()
            }.onFailure { e ->
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun loadReadme() {
        viewModelScope.launch(dispatchers.io) {
            _state.update { it.copy(isReadmeLoading = true) }
            getReadmeUseCase(owner, repo).onSuccess { content ->
                _state.update { it.copy(isReadmeLoading = false, readme = content) }
            }.onFailure {
                _state.update { it.copy(isReadmeLoading = false, readme = "Не удалось загрузить README") }
            }
        }
    }

    fun onShareClick() {
        state.value.repository?.htmlUrl?.let { url ->
            shareManager.shareText("Посмотри этот репозиторий: $url")
        }
    }

    fun setIssueDialogVisible(visible: Boolean) {
        _state.update { it.copy(isIssueDialogVisible = visible, issueError = null) }
    }

    fun createIssue(title: String, body: String) {
        if (title.isBlank()) return

        viewModelScope.launch(dispatchers.io) {
            _state.update { it.copy(isIssueSending = true, issueError = null) }
            createIssueUseCase(owner, repo, title, body).onSuccess {
                _state.update { it.copy(isIssueSending = false, isIssueDialogVisible = false) }
            }.onFailure { e ->
                _state.update { it.copy(isIssueSending = false, issueError = e.message) }
            }
        }
    }
}