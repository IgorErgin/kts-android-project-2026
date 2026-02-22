package com.github.igorergin.ktsandroid


import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.ktor3.KtorNetworkFetcherFactory
import com.github.igorergin.ktsandroid.presentation.ui.login.LoginScreen
import com.github.igorergin.ktsandroid.presentation.ui.welcome.WelcomeScreen
import kotlinx.serialization.Serializable

// --- ВАЖНО: Маршруты вынесены за пределы функции! ---
@Serializable
object Welcome

@Serializable
object Login

@Composable
fun App() {
    // Инициализация Coil для работы с сетью
    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context)
            .components {
                add(KtorNetworkFetcherFactory())
            }
            .build()
    }

    val navController = rememberNavController()

    MaterialTheme {
        NavHost(navController = navController, startDestination = Welcome) {
            composable<Welcome> {
                WelcomeScreen( onNavigateToLogin = { navController.navigate(Login) })
            }
            composable<Login> {
                LoginScreen()
            }
        }
    }
}