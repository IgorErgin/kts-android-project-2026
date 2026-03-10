package com.github.igorergin.ktsandroid.feature.auth.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.igorergin.ktsandroid.core.designsystem.components.AppButton
import com.github.igorergin.ktsandroid.feature.auth.domain.LocalAuthManager

@Composable
fun LoginScreen(
    onNavigateToMain: () -> Unit,
    loginViewModel: LoginViewModel = viewModel { LoginViewModel() }
) {
    val state by loginViewModel.state.collectAsStateWithLifecycle()

    // 1. Получаем кроссплатформенный менеджер авторизации
    val authManager = LocalAuthManager.current

    // 2. Подписываемся на получение кода авторизации с платформы (Android/iOS)
    LaunchedEffect(authManager) {
        authManager.authCodeFlow.collect { code ->
            // Как только платформа поймала Intent и передала код, отправляем его во ViewModel
            loginViewModel.handleOAuthCode(code)
        }
    }

    // Подписка на одноразовые события навигации (успешный вход)
    LaunchedEffect(Unit) {
        loginViewModel.events.collect { event ->
            when (event) {
                is LoginUiEvent.LoginSuccessEvent -> onNavigateToMain()
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