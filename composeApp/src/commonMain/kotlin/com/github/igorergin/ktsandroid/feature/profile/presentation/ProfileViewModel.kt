package com.github.igorergin.ktsandroid.feature.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.igorergin.ktsandroid.feature.auth.data.repository.GithubAuthRepository
import com.github.igorergin.ktsandroid.feature.profile.data.repository.ProfileRepository
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val profileRepository: ProfileRepository,
    private val authRepository: GithubAuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileUiState())
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            profileRepository.getCurrentUser()
                .onSuccess { user ->
                    _state.update { it.copy(profile = user, isLoading = false) }
                }
                .onFailure { e ->
                    Napier.e("Failed to load profile", e)
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun logout(onLogoutComplete: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
                .onSuccess { Napier.i("Logout successful") }
                .onFailure { Napier.e("Error during logout", it) }

            onLogoutComplete()
        }
    }
}