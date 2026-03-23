package com.github.igorergin.ktsandroid.feature.splash.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.github.igorergin.ktsandroid.core.navigation.Destination
import com.github.igorergin.ktsandroid.core.designsystem.theme.AppTheme


@Composable
fun SplashScreen(
    viewModel: SplashViewModel,
    onNavigate: (Destination) -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { dest ->
            onNavigate(dest)
        }
    }

    SplashContent()
}

@Composable
fun SplashContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Preview
@Composable
private fun SplashScreenPreview() {
    AppTheme {
        Surface {
            SplashContent()
        }
    }
}