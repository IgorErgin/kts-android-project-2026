package com.github.igorergin.ktsandroid.feature.repositories.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.igorergin.ktsandroid.feature.repositories.domain.repository.GithubRepoRepository
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: GithubRepoRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MainUiState())
    val state: StateFlow<MainUiState> = _state.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadData()
    }

    fun onSearchQueryChanged(newQuery: String) {
        _state.update { it.copy(query = newQuery) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500)
            if (newQuery.isNotBlank()) {
                loadData(isRefresh = true)
            }
        }
    }

    fun forceRefresh() {
        Napier.d("User triggered Pull-to-Refresh")
        loadData(isRefresh = true)
    }

    fun onListScrollPositionChanged(index: Int) {
        val currentState = _state.value
        if (index == currentState.repositories.lastIndex &&
            !currentState.isLoading &&
            !currentState.isPaginating &&
            !currentState.isRefreshing
        ) {
            loadData(isPagination = true)
        }
    }

    private fun loadData(isRefresh: Boolean = false, isPagination: Boolean = false) {
        val currentState = _state.value

        val targetPage = when {
            isRefresh -> 1
            isPagination -> currentState.page + 1
            else -> 1
        }

        _state.update {
            it.copy(
                isRefreshing = isRefresh,
                isPaginating = isPagination,
                isLoading = !isRefresh && !isPagination,
                error = null,
                page = targetPage
            )
        }

        viewModelScope.launch {
            repository.searchRepositories(
                query = currentState.query,
                page = targetPage,
                forceRefresh = isRefresh
            )
                .catch { e ->
                    Napier.e("Error loading data", e)
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            isPaginating = false,
                            error = "Ошибка загрузки: ${e.message}"
                        )
                    }
                }
                .collect { repos ->
                    _state.update {
                        it.copy(
                            repositories = repos,
                            isLoading = false,
                            isRefreshing = false,
                            isPaginating = false,
                            error = null
                        )
                    }
                }
        }
    }
}