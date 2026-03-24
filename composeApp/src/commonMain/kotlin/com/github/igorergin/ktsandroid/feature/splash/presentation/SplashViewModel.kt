package com.github.igorergin.ktsandroid.feature.splash.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.igorergin.ktsandroid.core.datastore.AppSettings
import com.github.igorergin.ktsandroid.core.datastore.TokenManager
import com.github.igorergin.ktsandroid.core.navigation.Destination
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SplashViewModel(
    private val appSettings: AppSettings,
    private val tokenManager: TokenManager
) : ViewModel() {


    private val _navigationEvent = MutableSharedFlow<Destination>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val navigationEvent = _navigationEvent.asSharedFlow()

    init {
        determineStartDestination()
    }

    private fun determineStartDestination() {
        viewModelScope.launch {
            try {
                tokenManager.initToken()

                val isFirstLaunch = appSettings.isFirstLaunchFlow.first()

                val dest = when {
                    isFirstLaunch -> Destination.Welcome
                    tokenManager.isLoggedIn() -> Destination.MainContainer
                    else -> Destination.Login
                }

                _navigationEvent.emit(dest)
            } catch (e: Exception) {
                _navigationEvent.emit(Destination.Welcome)
            }
        }
    }
}