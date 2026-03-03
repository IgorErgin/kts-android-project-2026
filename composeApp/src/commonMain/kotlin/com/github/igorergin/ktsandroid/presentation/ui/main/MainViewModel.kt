package com.github.igorergin.ktsandroid.presentation.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.igorergin.ktsandroid.data.ProfileRepository
import com.github.igorergin.ktsandroid.domain.model.Post
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val profileRepository: ProfileRepository = ProfileRepository()
) : ViewModel() {

    private val _state = MutableStateFlow(MainUiState())
    val state: StateFlow<MainUiState> = _state.asStateFlow()

    // Моковые посты для демонстрации списка
    val dummyPosts = listOf(
        Post(1, "Мой первый пост в новом приложении VK!", "Только что"),
        Post(2, "Изучаю Compose Multiplatform. Очень крутая штука.", "Вчера"),
        Post(3, "Всем привет! Как у вас дела?", "2 дня назад")
    )

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val result = profileRepository.getProfile()

            result.onSuccess { profile ->
                _state.update { it.copy(profile = profile, isLoading = false) }
            }

            result.onFailure { exception ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = exception.message ?: "Ошибка загрузки профиля"
                    )
                }
            }
        }
    }
}