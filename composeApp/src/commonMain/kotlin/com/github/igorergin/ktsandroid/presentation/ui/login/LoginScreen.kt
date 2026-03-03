package com.github.igorergin.ktsandroid.presentation.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.igorergin.ktsandroid.presentation.common.VKButton
import com.github.igorergin.ktsandroid.presentation.common.VKTextField
import com.github.igorergin.ktsandroid.theme.AppTheme
import ktsandroidproject.composeapp.generated.resources.Res
import ktsandroidproject.composeapp.generated.resources.login_button
import ktsandroidproject.composeapp.generated.resources.login_label
import ktsandroidproject.composeapp.generated.resources.login_title
import ktsandroidproject.composeapp.generated.resources.password_label
import org.jetbrains.compose.resources.stringResource

@Composable
fun LoginScreen(
    onNavigateToMain: () -> Unit,
    loginViewModel: LoginViewModel = viewModel { LoginViewModel() }
) {
    val state by loginViewModel.state.collectAsState()

    // Подписка на события навигации
    LaunchedEffect(Unit) {
        loginViewModel.events.collect { event ->
            when (event) {
                is LoginUiEvent.LoginSuccessEvent -> {
                    onNavigateToMain()
                }
            }
        }
    }

    // Кэширование лямбд для оптимизации рекомпозиций
    val onLoginChanged = remember { { text: String -> loginViewModel.onLoginChanged(text) } }
    val onPasswordChanged = remember { { text: String -> loginViewModel.onPasswordChanged(text) } }
    val onLoginClick = remember { { loginViewModel.onLoginClick() } }

    LoginScreenContent(
        state = state,
        onLoginChanged = onLoginChanged,
        onPasswordChanged = onPasswordChanged,
        onLoginClick = onLoginClick
    )
}

@Composable
fun LoginScreenContent(
    state: LoginUiState,
    onLoginChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onLoginClick: () -> Unit
) {
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(Res.string.login_title),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(32.dp))

                VKTextField(
                    value = state.login,
                    onValueChange = onLoginChanged,
                    label = stringResource(Res.string.login_label),
                    isError = state.error != null
                )

                Spacer(modifier = Modifier.height(16.dp))

                VKTextField(
                    value = state.password,
                    onValueChange = onPasswordChanged,
                    label = stringResource(Res.string.password_label),
                    isError = state.error != null,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, contentDescription = null)
                        }
                    }
                )

                // Ошибка авторизации
                state.error?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                VKButton(
                    text = stringResource(Res.string.login_button),
                    onClick = onLoginClick,
                    enabled = state.isLoginButtonActive,
                    isLoading = state.isLoading
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    AppTheme {
        LoginScreenContent(
            state = LoginUiState(login = "admin", password = "123", isLoginButtonActive = true),
            onLoginChanged = {},
            onPasswordChanged = {},
            onLoginClick = {}
        )
    }
}