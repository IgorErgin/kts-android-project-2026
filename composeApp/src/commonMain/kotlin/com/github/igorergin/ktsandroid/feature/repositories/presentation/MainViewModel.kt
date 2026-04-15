package com.github.igorergin.ktsandroid.feature.repositories.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.igorergin.ktsandroid.core.domain.error.AppError
import com.github.igorergin.ktsandroid.core.domain.error.AppErrorException
import com.github.igorergin.ktsandroid.core.util.AppDispatchers
import com.github.igorergin.ktsandroid.feature.repositories.domain.model.GithubRepository
import com.github.igorergin.ktsandroid.feature.repositories.domain.usecase.SearchRepositoriesUseCase
import com.github.igorergin.ktsandroid.feature.repositories.domain.usecase.ToggleFavoriteUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface MainSideEffect {
    data class Error(val error: AppError) : MainSideEffect
}

class MainViewModel(
    private val searchRepositoriesUseCase: SearchRepositoriesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val dispatchers: AppDispatchers
) : ViewModel() {

    private val _state = MutableStateFlow(MainUiState())
    val state: StateFlow<MainUiState> = _state.asStateFlow()

    private val _sideEffect = Channel<MainSideEffect>(capacity = Channel.BUFFERED)
    val sideEffect = _sideEffect.receiveAsFlow()

    private var searchJob: Job? = null

    init {
        observeFavorites()
        handleIntent(MainIntent.SearchQueryChanged(""))
    }

    private fun observeFavorites() {
        searchRepositoriesUseCase.getFavoritesFlow()
            .onEach { favorites ->
                val favoriteIds = favorites.map { it.id }.toSet()
                _state.update { currentState ->
                    currentState.copy(
                        repositories = currentState.repositories.map { repo ->
                            repo.copy(isFavorite = favoriteIds.contains(repo.id))
                        }
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun handleIntent(intent: MainIntent) {
        when (intent) {
            is MainIntent.SearchQueryChanged -> updateQuery(intent.query)
            is MainIntent.Refresh -> loadData(isRefresh = true)
            is MainIntent.LoadNextPage -> loadData(isPagination = true)
            is MainIntent.ToggleFavorite -> toggleFavorite(intent.repo)
        }
    }

    private fun updateQuery(newQuery: String) {
        _state.update { it.copy(query = newQuery) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch(dispatchers.main) {
            delay(500)
            loadData(isRefresh = true)
        }
    }

    private fun loadData(isRefresh: Boolean = false, isPagination: Boolean = false) {
        val currentState = _state.value

        if (currentState.isLoading || currentState.isRefreshing || currentState.isPaginating) return

        val targetPage = if (isRefresh) 1 else currentState.page + 1

        _state.update {
            it.copy(
                isRefreshing = isRefresh,
                isPaginating = isPagination,
                isLoading = !isRefresh && !isPagination && currentState.repositories.isEmpty(),
                error = null,
                page = targetPage
            )
        }

        viewModelScope.launch(dispatchers.io) {
            var hasReceivedNetworkData = false
            searchRepositoriesUseCase(
                query = currentState.query,
                page = targetPage,
                forceRefresh = isRefresh
            )
                .catch { e ->
                    if (e is CancellationException) throw e
                    val error = (e as? AppErrorException)?.error ?: AppError.Unknown(e.message)
                    
                    if (isPagination) {
                        _sideEffect.send(MainSideEffect.Error(error))
                    }

                    _state.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            isPaginating = false,
                            error = if (isPagination) it.error else error
                        )
                    }
                }
                .collect { repos ->
                    _state.update { currentState ->
                        val newRepos = if (isPagination) currentState.repositories + repos else repos
                        val favorites = searchRepositoriesUseCase.getFavoritesFlow().first().map { it.id }.toSet()
                        
                        currentState.copy(
                            repositories = newRepos.distinctBy { r -> r.id }.map { repo ->
                                repo.copy(isFavorite = favorites.contains(repo.id))
                            },
                            isLoading = false,
                            isRefreshing = false,
                            isPaginating = false,
                            error = null,
                            isOfflineData = !hasReceivedNetworkData && currentState.page == 1 && repos.isNotEmpty()
                        )
                    }
                    hasReceivedNetworkData = true
                }
        }
    }

    private fun toggleFavorite(repo: GithubRepository) {
        viewModelScope.launch(dispatchers.io) {
            toggleFavoriteUseCase(repo).onFailure { e: Throwable ->
                io.github.aakira.napier.Napier.e("Failed to toggle favorite on server", e)
            }
        }
    }
}
