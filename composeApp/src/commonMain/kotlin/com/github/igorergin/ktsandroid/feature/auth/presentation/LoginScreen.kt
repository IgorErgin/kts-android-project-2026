package com.github.igorergin.ktsandroid.feature.auth.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.github.igorergin.ktsandroid.core.designsystem.common.AppButton
import com.github.igorergin.ktsandroid.core.designsystem.theme.AppTheme
import com.github.igorergin.ktsandroid.feature.auth.domain.AuthManager
import kotlinx.coroutines.flow.MutableSharedFlow

@Composable
fun LoginScreen(
    onNavigateToMain: () -> Unit,
    loginViewModel: LoginViewModel
) {
    val state by loginViewModel.state.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    // 1. Получаем кроссплатформенный менеджер авторизации
    val authManager = LocalAuthManager.current

    // 2. Подписываемся на получение кода авторизации с платформы (Android/iOS)
    LaunchedEffect(authManager) {
        authManager.authCodeFlow.collect { code ->
            loginViewModel.handleOAuthCode(code)
        }
    }

    // Подписка на одноразовые события навигации (успешный вход)
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            loginViewModel.events.collect { event ->
                when (event) {
                    is LoginUiEvent.LoginSuccessEvent -> onNavigateToMain()
                }
            }
        }
    }

    // 3. По клику делегируем открытие OAuth платформенному менеджеру
    val onLoginClick = remember {
        {
            authManager.launchAuthFlow()
        }
    }

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Вход через GitHub",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(32.dp))

                state.error?.let { errorMsg ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = errorMsg,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                AppButton(
                    text = if (state.isLoading) "Авторизация..." else "Войти с GitHub",
                    onClick = onLoginClick,
                    isLoading = state.isLoading
                )
            }
        }
    }
}

private class FakeAuthManager : AuthManager {
    override val authCodeFlow = MutableSharedFlow<String>()
    override fun launchAuthFlow() {}
    override fun dispose() {}
}

@Preview
@Composable
private fun LoginScreenPreview() {
    val fakeAuthManager = remember { FakeAuthManager() }

    AppTheme {
        CompositionLocalProvider(LocalAuthManager provides fakeAuthManager) {
            Surface(modifier = Modifier.fillMaxSize()) {
                LoginScreen(
                    onNavigateToMain = {},
                    loginViewModel = LoginViewModel()
                )
            }
        }
    }
}

@Preview
@Composable
private fun LoginScreenErrorPreview() {
    val fakeAuthManager = remember { FakeAuthManager() }

    AppTheme {
        CompositionLocalProvider(LocalAuthManager provides fakeAuthManager) {
            Surface(modifier = Modifier.fillMaxSize()) {
                LoginScreen(
                    onNavigateToMain = {},
                    loginViewModel = LoginViewModel().apply {
                    }
                )
            }
        }
    }
}