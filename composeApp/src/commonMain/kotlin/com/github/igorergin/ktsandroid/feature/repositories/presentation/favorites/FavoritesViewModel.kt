package com.github.igorergin.ktsandroid.feature.repositories.presentation.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.igorergin.ktsandroid.core.util.AppDispatchers
import com.github.igorergin.ktsandroid.feature.repositories.domain.model.GithubRepository
import com.github.igorergin.ktsandroid.feature.repositories.domain.usecase.GetFavoritesUseCase
import com.github.igorergin.ktsandroid.feature.repositories.domain.usecase.ToggleFavoriteUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val getFavoritesUseCase: GetFavoritesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val dispatchers: AppDispatchers
) : ViewModel() {

    private val _state = MutableStateFlow(FavoritesUiState())
    val state: StateFlow<FavoritesUiState> = _state.asStateFlow()

    init {
        loadFavorites()
    }

    private fun loadFavorites() {
        getFavoritesUseCase()
            .flowOn(dispatchers.io)
            .onStart { _state.update { it.copy(isLoading = true) } }
            .onEach { favorites ->
                _state.update { it.copy(isLoading = false, favorites = favorites) }
            }
            .catch { e ->
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
            .launchIn(viewModelScope)
    }

    fun toggleFavorite(repo: GithubRepository) {
        viewModelScope.launch(dispatchers.io) {
            toggleFavoriteUseCase(repo)
        }
    }
}

data class FavoritesUiState(
    val favorites: List<GithubRepository> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)