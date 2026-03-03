package com.github.igorergin.ktsandroid.presentation.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.igorergin.ktsandroid.data.LoginRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginRepository: LoginRepository = LoginRepository()
) : ViewModel() {
    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<LoginUiEvent>()
    val events: SharedFlow<LoginUiEvent> = _events.asSharedFlow()

    fun onLoginChanged(newLogin: String) {
        _state.update { currentState ->
            val newState = currentState.copy(login = newLogin, error = null)
            newState.copy(isLoginButtonActive = isInputValid(newState))
        }
    }

    fun onPasswordChanged(newPassword: String) {
        _state.update { currentState ->
            val newState = currentState.copy(password = newPassword, error = null)
            newState.copy(isLoginButtonActive = isInputValid(newState))
        }
    }

    private fun isInputValid(state: LoginUiState): Boolean {
        return state.login.isNotBlank() && state.password.isNotBlank()
    }

    fun onLoginClick() {
        val currentState = _state.value
        if (!currentState.isLoginButtonActive) return

        _state.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            val result = loginRepository.login(currentState.login, currentState.password)

            result.onSuccess {
                _state.update { it.copy(isLoading = false) }
                _events.emit(LoginUiEvent.LoginSuccessEvent)
            }

            result.onFailure { exception ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = exception.message ?: "Произошла неизвестная ошибка"
                    )
                }
            }
        }
    }
}