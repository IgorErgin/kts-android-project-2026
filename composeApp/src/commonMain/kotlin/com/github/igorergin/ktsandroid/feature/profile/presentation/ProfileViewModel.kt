package com.github.igorergin.ktsandroid.feature.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.igorergin.ktsandroid.core.datastore.TokenManager
import com.github.igorergin.ktsandroid.feature.profile.data.repository.ProfileRepository
import com.github.igorergin.ktsandroid.feature.repositories.domain.repository.GithubRepoRepository
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val profileRepository: ProfileRepository,
    private val tokenManager: TokenManager,
    private val githubRepoRepository: GithubRepoRepository // Нужен для очистки Room
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileUiState())
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val user = profileRepository.getCurrentUser()
                _state.update { it.copy(profile = user, isLoading = false) }
            } catch (e: Exception) {
                Napier.e("Failed to load profile", e)
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }


    fun logout(onLogoutComplete: () -> Unit) {
        viewModelScope.launch {
            try {
                githubRepoRepository.clearLocalData()

                tokenManager.clear()

                Napier.i("Logout successful. All local data cleared.")

                onLogoutComplete()
            } catch (e: Exception) {
                Napier.e("Error during logout", e)
                tokenManager.clear()
                onLogoutComplete()
            }
        }
    }
}