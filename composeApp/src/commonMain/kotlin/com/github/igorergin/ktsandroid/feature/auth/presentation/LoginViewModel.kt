package com.github.igorergin.ktsandroid.feature.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.igorergin.ktsandroid.feature.auth.data.repository.GithubAuthRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: GithubAuthRepository
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
                .onSuccess {
                    _state.update { it.copy(isLoading = false) }
                    _events.emit(LoginUiEvent.LoginSuccessEvent)
                }
                .onFailure { e ->
                    _state.update { it.copy(isLoading = false, error = "Ошибка авторизации: ${e.message}") }
                }
        }
    }
}