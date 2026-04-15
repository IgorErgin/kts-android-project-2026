package com.github.igorergin.ktsandroid.feature.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.igorergin.ktsandroid.core.domain.error.AppError
import com.github.igorergin.ktsandroid.core.domain.error.AppErrorException
import com.github.igorergin.ktsandroid.core.util.AppDispatchers
import com.github.igorergin.ktsandroid.feature.auth.data.repository.GithubAuthRepository
import com.github.igorergin.ktsandroid.feature.profile.domain.usecase.GetProfileUseCase
import com.github.igorergin.ktsandroid.feature.profile.domain.usecase.GetUserActivityUseCase
import com.github.igorergin.ktsandroid.feature.profile.domain.usecase.GetUserReposUseCase
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val getProfileUseCase: GetProfileUseCase,
    private val getUserReposUseCase: GetUserReposUseCase,
    private val getUserActivityUseCase: GetUserActivityUseCase,
    private val authRepository: GithubAuthRepository,
    private val dispatchers: AppDispatchers
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileUiState())
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch(dispatchers.io) {
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                val profile = getProfileUseCase().getOrThrow()
                val repos = getUserReposUseCase().getOrThrow()
                
                _state.update {
                    it.copy(
                        profile = profile,
                        repos = repos,
                        isLoading = false
                    )
                }
                loadActivity(profile.login)
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                Napier.e("Failed to load profile or repos", e)
                val appError = (e as? AppErrorException)?.error ?: AppError.Unknown(e.message)
                _state.update { it.copy(isLoading = false, error = appError) }
            }
        }
    }

    private fun loadActivity(username: String) {
        viewModelScope.launch(dispatchers.io) {
            _state.update { it.copy(isActivityLoading = true) }
            getUserActivityUseCase(username)
                .onSuccess { events ->
                    _state.update { it.copy(events = events, isActivityLoading = false) }
                }
                .onFailure { e ->
                    if (e is CancellationException) throw e
                    Napier.e("Failed to load activity", e)
                    _state.update { it.copy(isActivityLoading = false) }
                }
        }
    }

    fun onTabSelected(index: Int) {
        _state.update { it.copy(selectedTab = index) }
    }

    fun logout(onLogoutComplete: () -> Unit) {
        viewModelScope.launch(dispatchers.io) {
            authRepository.logout()
                .onSuccess { 
                    Napier.i("Logout successful")
                    onLogoutComplete()
                }
                .onFailure { 
                    Napier.e("Error during logout", it)
                    onLogoutComplete()
                }
        }
    }
}
