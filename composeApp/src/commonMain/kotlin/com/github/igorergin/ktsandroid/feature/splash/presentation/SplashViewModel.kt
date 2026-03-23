package com.github.igorergin.ktsandroid.feature.splash.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.igorergin.ktsandroid.core.datastore.AppSettings
import com.github.igorergin.ktsandroid.core.datastore.TokenManager
import com.github.igorergin.ktsandroid.core.navigation.Destination
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class SplashViewModel(
    private val appSettings: AppSettings,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _navigationEvent = MutableSharedFlow<Destination>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    init {
        determineStartDestination()
    }

    private fun determineStartDestination() {
        viewModelScope.launch {
            tokenManager.initToken()
            val isFirstLaunch = appSettings.isFirstLaunchFlow.firstOrNull() ?: true

            val dest = when {
                isFirstLaunch -> Destination.Welcome
                tokenManager.isLoggedIn() -> Destination.Main
                else -> Destination.Login
            }
            _navigationEvent.emit(dest)
        }
    }
}