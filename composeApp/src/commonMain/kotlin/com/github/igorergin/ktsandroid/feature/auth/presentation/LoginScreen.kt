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
import ktsandroidproject.composeapp.generated.resources.Res
import ktsandroidproject.composeapp.generated.resources.auth_with_github
import ktsandroidproject.composeapp.generated.resources.login_description
import ktsandroidproject.composeapp.generated.resources.login_title
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource


@Composable
fun LoginScreen(
    onNavigateToMain: () -> Unit,
    loginViewModel: LoginViewModel
) {
    val state by loginViewModel.state.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    val authManager = LocalAuthManager.current

    LaunchedEffect(authManager) {
        authManager.authCodeFlow.collect { code ->
            loginViewModel.handleOAuthCode(code)
        }
    }

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            loginViewModel.events.collect { event ->
                when (event) {
                    is LoginUiEvent.LoginSuccessEvent -> onNavigateToMain()
                }
            }
        }
    }

    val onLoginClick = remember {
        {
            authManager.launchAuthFlow()
        }
    }

    LoginContent(
        state = state,
        onLoginClick = onLoginClick
    )
}

@Composable
fun LoginContent(
    state: LoginUiState,
    onLoginClick: () -> Unit
) {
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
                    text = stringResource(Res.string.login_description),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(32.dp))

                state.error?.let { errorMsg ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
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
                    text = if (state.isLoading) stringResource(Res.string.login_title) else stringResource(
                        Res.string.auth_with_github
                    ),
                    onClick = onLoginClick,
                    isLoading = state.isLoading
                )
            }
        }
    }
}


@Preview
@Composable
private fun LoginScreenPreview() {
    AppTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            LoginContent(
                state = LoginUiState(isLoading = false, error = null),
                onLoginClick = {}
            )
        }
    }
}

@Preview
@Composable
private fun LoginScreenErrorPreview() {
    AppTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            LoginContent(
                state = LoginUiState(
                    isLoading = false,
                    error = "Неверный логин или пароль. Попробуйте снова."
                ),
                onLoginClick = {}
            )
        }
    }
}

@Preview
@Composable
private fun LoginScreenLoadingPreview() {
    AppTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            LoginContent(
                state = LoginUiState(isLoading = true, error = null),
                onLoginClick = {}
            )
        }
    }
}