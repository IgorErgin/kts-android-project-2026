package com.github.igorergin.ktsandroid.feature.detail.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.igorergin.ktsandroid.feature.detail.data.repository.DetailRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetailViewModel(
    private val owner: String,
    private val repo: String,
    private val repository: DetailRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DetailUiState())
    val state = _state.asStateFlow()

    init {
        loadRepository()
    }

    fun loadRepository() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            repository.getRepositoryDetails(owner, repo)
                .onSuccess { data ->
                    _state.update { it.copy(isLoading = false, repository = data) }
                }
                .onFailure { e ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = e.message ?: "Неизвестная ошибка"
                        )
                    }
                }
        }
    }
}