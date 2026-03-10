package com.github.igorergin.ktsandroid.feature.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.igorergin.ktsandroid.core.datastore.TokenStorage
import com.github.igorergin.ktsandroid.feature.auth.data.repository.GithubAuthRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: GithubAuthRepository = GithubAuthRepository()
) : ViewModel() {
    private val _state = MutableStateFlow(LoginUiState())
    val state = _state.asStateFlow()

    private val _events = MutableSharedFlow<LoginUiEvent>()
    val events = _events.asSharedFlow()

    fun handleOAuthCode(code: String) {
        if (_state.value.isLoading) return

        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            authRepository.exchangeCodeForToken(code)
                .onSuccess { token ->
                    TokenStorage.saveToken(token)
                    _state.update { it.copy(isLoading = false) }
                    _events.emit(LoginUiEvent.LoginSuccessEvent)
                }
                .onFailure { e ->
                    _state.update { it.copy(isLoading = false, error = "Ошибка авторизации: ${e.message}") }
                }
        }
    }
}