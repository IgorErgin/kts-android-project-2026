package com.github.igorergin.ktsandroid.feature.detail.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.igorergin.ktsandroid.core.domain.error.AppError
import com.github.igorergin.ktsandroid.core.domain.error.AppErrorException
import com.github.igorergin.ktsandroid.core.util.AppDispatchers
import com.github.igorergin.ktsandroid.core.util.ShareManager
import com.github.igorergin.ktsandroid.feature.detail.domain.model.GithubContent
import com.github.igorergin.ktsandroid.feature.detail.domain.model.PullRequest
import com.github.igorergin.ktsandroid.feature.detail.domain.usecase.CreateIssueUseCase
import com.github.igorergin.ktsandroid.feature.detail.domain.usecase.GetContentUseCase
import com.github.igorergin.ktsandroid.feature.detail.domain.usecase.GetPullRequestsUseCase
import com.github.igorergin.ktsandroid.feature.detail.domain.usecase.GetReadmeUseCase
import com.github.igorergin.ktsandroid.feature.detail.domain.usecase.GetRepositoryDetailsUseCase
import com.github.igorergin.ktsandroid.feature.detail.domain.usecase.UploadFileUseCase
import com.github.igorergin.ktsandroid.feature.repositories.domain.model.GithubRepository
import com.github.igorergin.ktsandroid.feature.repositories.domain.usecase.GetFavoritesUseCase
import com.github.igorergin.ktsandroid.feature.repositories.domain.usecase.ToggleFavoriteUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ktsandroidproject.composeapp.generated.resources.Res
import ktsandroidproject.composeapp.generated.resources.share_text
import ktsandroidproject.composeapp.generated.resources.success_file_uploaded
import ktsandroidproject.composeapp.generated.resources.success_issue_created
import org.jetbrains.compose.resources.getString

class DetailViewModel(
    private val owner: String,
    private val repo: String,
    private val getRepositoryDetailsUseCase: GetRepositoryDetailsUseCase,
    private val getReadmeUseCase: GetReadmeUseCase,
    private val createIssueUseCase: CreateIssueUseCase,
    private val getContentUseCase: GetContentUseCase,
    private val uploadFileUseCase: UploadFileUseCase,
    private val getPullRequestsUseCase: GetPullRequestsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val getFavoritesUseCase: GetFavoritesUseCase,
    private val shareManager: ShareManager,
    private val dispatchers: AppDispatchers
) : ViewModel() {

    private val _state = MutableStateFlow(DetailUiState())
    val state = _state.asStateFlow()

    private val _sideEffect = Channel<DetailSideEffect>(capacity = Channel.BUFFERED)
    val sideEffect = _sideEffect.receiveAsFlow()

    init {
        loadAll()
        loadPullRequests()
        observeFavorites()
    }

    private fun observeFavorites() {
        getFavoritesUseCase()
            .onEach { favorites ->
                val isFavorite = favorites.any { it.ownerName == owner && it.name == repo }
                _state.update { it.copy(isFavorite = isFavorite) }
            }
            .launchIn(viewModelScope)
    }

    fun loadAll() {
        viewModelScope.launch(dispatchers.io) {
            _state.update { it.copy(isLoading = true, error = null) }

            getRepositoryDetailsUseCase(owner, repo).onSuccess { data ->
                _state.update { it.copy(isLoading = false, repository = data) }
                loadReadme()
                loadContents()
            }.onFailure { e ->
                if (e is CancellationException) throw e
                val error = (e as? AppErrorException)?.error ?: AppError.Unknown(e.message)
                _state.update { it.copy(isLoading = false, error = error) }
            }
        }
    }

    fun toggleFavorite() {
        val repoDetail = _state.value.repository ?: return
        val currentFavoriteStatus = _state.value.isFavorite
        
        // Optimistic update
        _state.update { it.copy(isFavorite = !currentFavoriteStatus) }
        
        viewModelScope.launch(dispatchers.io) {
            val githubRepo = GithubRepository(
                id = repoDetail.id,
                name = repoDetail.name,
                fullName = repoDetail.fullName,
                description = repoDetail.description,
                language = repoDetail.language ?: "",
                starsCount = repoDetail.starsCount,
                ownerName = repoDetail.ownerLogin,
                ownerAvatarUrl = repoDetail.ownerAvatarUrl,
                isFavorite = currentFavoriteStatus
            )
            
            toggleFavoriteUseCase(githubRepo).onFailure { e ->
                if (e is CancellationException) throw e
                // Rollback on failure
                _state.update { it.copy(isFavorite = currentFavoriteStatus) }
                val error = (e as? AppErrorException)?.error ?: AppError.Unknown(e.message)
                _sideEffect.send(DetailSideEffect.ShowError(error))
            }
        }
    }

    private fun loadReadme() {
        viewModelScope.launch(dispatchers.io) {
            _state.update { it.copy(isReadmeLoading = true) }
            getReadmeUseCase(owner, repo).onSuccess { content ->
                _state.update { it.copy(isReadmeLoading = false, readme = content) }
            }.onFailure { e ->
                if (e is CancellationException) throw e
                _state.update { it.copy(isReadmeLoading = false, readme = null) }
            }
        }
    }

    fun loadContents(path: String = "") {
        viewModelScope.launch(dispatchers.io) {
            _state.update { it.copy(isContentsLoading = true, contentsError = null, currentPath = path) }
            getContentUseCase(owner, repo, path).onSuccess { content ->
                _state.update { it.copy(isContentsLoading = false, contents = content) }
            }.onFailure { e ->
                if (e is CancellationException) throw e
                val error = (e as? AppErrorException)?.error ?: AppError.Unknown(e.message)
                _state.update { it.copy(isContentsLoading = false, contentsError = error) }
            }
        }
    }

    fun uploadFile(path: String, message: String, contentBase64: String) {
        viewModelScope.launch(dispatchers.io) {
            _state.update { it.copy(isUploading = true, uploadError = null) }

            val existingFileSha = _state.value.contents
                .filterIsInstance<GithubContent.File>()
                .find { it.name == path.substringAfterLast("/") }?.sha

            uploadFileUseCase(owner, repo, path, message, contentBase64, existingFileSha).onSuccess {
                _state.update { it.copy(isUploading = false, isUploadDialogVisible = false) }
                _sideEffect.send(DetailSideEffect.ShowMessage(Res.string.success_file_uploaded))
                loadContents(_state.value.currentPath)
            }.onFailure { e ->
                if (e is CancellationException) throw e
                val error = (e as? AppErrorException)?.error ?: AppError.Unknown(e.message)
                _state.update { it.copy(isUploading = false, uploadError = error) }
            }
        }
    }

    fun setUploadDialogVisible(visible: Boolean) {
        _state.update { it.copy(isUploadDialogVisible = visible, uploadError = null) }
    }

    fun onShareClick() {
        viewModelScope.launch {
            state.value.repository?.htmlUrl?.let { url ->
                val text = getString(Res.string.share_text, url)
                shareManager.shareText(text)
            }
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
                _sideEffect.send(DetailSideEffect.ShowMessage(Res.string.success_issue_created))
            }.onFailure { e ->
                if (e is CancellationException) throw e
                val error = (e as? AppErrorException)?.error ?: AppError.Unknown(e.message)
                _state.update { it.copy(isIssueSending = false, issueError = error) }
            }
        }
    }

    fun onFileClick(file: GithubContent.File) {
        _state.update { it.copy(selectedFile = file) }
    }

    fun closeFileViewer() {
        _state.update { it.copy(selectedFile = null) }
    }

    fun onPullRequestClick(pr: PullRequest) {
        _state.update { it.copy(selectedPullRequest = pr) }
    }

    fun closePullRequestDetail() {
        _state.update { it.copy(selectedPullRequest = null) }
    }

    fun onTabSelected(index: Int) {
        _state.update { it.copy(selectedTabIndex = index) }
    }

    fun loadPullRequests() {
        viewModelScope.launch(dispatchers.io) {
            _state.update { it.copy(isPullsLoading = true, pullsError = null) }
            getPullRequestsUseCase(owner, repo).onSuccess { pulls ->
                _state.update { it.copy(isPullsLoading = false, pullRequests = pulls) }
            }.onFailure { e ->
                if (e is CancellationException) throw e
                val error = (e as? AppErrorException)?.error ?: AppError.Unknown(e.message)
                _state.update { it.copy(isPullsLoading = false, pullsError = error) }
            }
        }
    }
}
