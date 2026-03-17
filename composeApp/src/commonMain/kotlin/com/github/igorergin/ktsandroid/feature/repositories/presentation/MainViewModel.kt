package com.github.igorergin.ktsandroid.feature.repositories.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.igorergin.ktsandroid.feature.repositories.data.repository.GithubRepoRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class MainViewModel(
    private val repository: GithubRepoRepository = GithubRepoRepository()
) : ViewModel() {

    private val _state = MutableStateFlow(MainUiState())
    val state = _state.asStateFlow()

    private val searchQueryFlow = MutableStateFlow("")
    private var currentPage = 1

    init {
        viewModelScope.launch {
            searchQueryFlow
                .debounce(500L)
                .distinctUntilChanged()
                .flatMapLatest { query ->
                    flow {
                        if (query.isBlank()) {
                            emit(Result.success(emptyList())); return@flow
                        }
                        _state.update { it.copy(isLoading = true, error = null, isEmpty = false) }
                        currentPage = 1
                        emit(repository.searchRepositories(query, currentPage))
                    }
                }
                .collect { result ->
                    result.onSuccess { items ->
                        _state.update {
                            it.copy(
                                isLoading = false,
                                repositories = items,
                                isEmpty = items.isEmpty(),
                                isPaginationExhausted = items.isEmpty()
                            )
                        }
                    }
                    result.onFailure { e ->
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = "Ошибка: ${e.message}",
                                repositories = emptyList()
                            )
                        }
                    }
                }
        }
    }

    fun onSearchQueryChanged(newQuery: String) {
        _state.update { it.copy(query = newQuery) }
        searchQueryFlow.value = newQuery
    }

    fun loadNextPage() {
        val q = searchQueryFlow.value
        if (q.isBlank() || _state.value.isPaginating || _state.value.isPaginationExhausted || _state.value.isLoading) return

        viewModelScope.launch {
            _state.update { it.copy(isPaginating = true) }
            currentPage++
            repository.searchRepositories(q, currentPage)
                .onSuccess { newItems ->
                    _state.update {
                        it.copy(
                            isPaginating = false,
                            repositories = it.repositories + newItems,
                            isPaginationExhausted = newItems.isEmpty()
                        )
                    }
                }
                .onFailure { e ->
                    _state.update { it.copy(isPaginating = false, error = e.message) }
                    currentPage--
                }
        }
    }

    fun retry() {
        val q = searchQueryFlow.value; searchQueryFlow.value = ""; searchQueryFlow.value = q
    }
}