package com.github.igorergin.ktsandroid.feature.repositories.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.igorergin.ktsandroid.core.util.AppDispatchers
import com.github.igorergin.ktsandroid.feature.repositories.domain.usecase.SearchRepositoriesUseCase
import com.github.igorergin.ktsandroid.feature.repositories.domain.usecase.ToggleFavoriteUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(
    private val searchRepositoriesUseCase: SearchRepositoriesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val dispatchers: AppDispatchers
) : ViewModel() {

    private val _state = MutableStateFlow(MainUiState())
    val state: StateFlow<MainUiState> = _state.asStateFlow()

    private var searchJob: Job? = null

    init {
        handleIntent(MainIntent.SearchQueryChanged(""))
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
            searchRepositoriesUseCase(
                query = currentState.query,
                page = targetPage,
                forceRefresh = isRefresh
            )
                .catch { e ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            isPaginating = false,
                            error = "Ошибка: ${e.message}"
                        )
                    }
                }
                .collect { repos ->
                    _state.update {
                        it.copy(
                            repositories = if (isPagination) it.repositories + repos else repos,
                            isLoading = false,
                            isRefreshing = false,
                            isPaginating = false,
                            error = null
                        )
                    }
                }
        }
    }

    private fun toggleFavorite(repo: com.github.igorergin.ktsandroid.feature.repositories.domain.model.GithubRepository) {
        viewModelScope.launch(dispatchers.io) {
            toggleFavoriteUseCase(repo)
        }
    }
}