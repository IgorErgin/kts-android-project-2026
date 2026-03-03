package com.github.igorergin.ktsandroid

import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.ktor3.KtorNetworkFetcherFactory
import com.github.igorergin.ktsandroid.presentation.ui.login.LoginScreen
import com.github.igorergin.ktsandroid.presentation.ui.main.MainScreen
import com.github.igorergin.ktsandroid.presentation.ui.welcome.WelcomeScreen
import com.github.igorergin.ktsandroid.theme.AppTheme
import kotlinx.serialization.Serializable

@Serializable object Welcome
@Serializable object Login
@Serializable object Main

@Composable
fun App() {
    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context)
            .components { add(KtorNetworkFetcherFactory()) }
            .build()
    }

    val navController = rememberNavController()

    AppTheme {
        NavHost(
            navController = navController,
            startDestination = Welcome
        ) {
            composable<Welcome> {
                WelcomeScreen(
                    onNavigateToLogin = { navController.navigate(Login) }
                )
            }

            composable<Login> {
                LoginScreen(
                    onNavigateToMain = {
                        navController.navigate(Main) {
                            popUpTo(Welcome) { inclusive = true }
                        }
                    }
                )
            }

            composable<Main> {
                MainScreen()
            }
        }
    }
}