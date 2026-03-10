package com.github.igorergin.ktsandroid

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.ktor3.KtorNetworkFetcherFactory
import com.github.igorergin.ktsandroid.core.designsystem.theme.AppTheme
import com.github.igorergin.ktsandroid.feature.onboarding.presentation.WelcomeScreen
import com.github.igorergin.ktsandroid.feature.auth.presentation.LoginScreen
import com.github.igorergin.ktsandroid.feature.repositories.presentation.MainScreen
import com.github.igorergin.ktsandroid.feature.detail.presentation.DetailScreen
import com.github.igorergin.ktsandroid.core.network.GithubAuthConfig
import kotlinx.serialization.Serializable

@Serializable
object Welcome
@Serializable
data class Login(val code: String? = null)
@Serializable
object Main
@Serializable
data class Detail(val owner: String, val repo: String)

@Composable
fun App() {
    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context).components { add(KtorNetworkFetcherFactory()) }.build()
    }
    val navController = rememberNavController()

    AppTheme {
        NavHost(navController = navController, startDestination = Welcome) {
            composable<Welcome> { WelcomeScreen { navController.navigate(Login(null)) } }

            composable<Login>(
                deepLinks = listOf(navDeepLink<Login>(basePath = GithubAuthConfig.REDIRECT_URI))
            ) { backStackEntry ->
                val loginDest = backStackEntry.toRoute<Login>()

                LoginScreen(
                    onNavigateToMain = {
                        navController.navigate(Main) {
                            popUpTo(Welcome) { inclusive = true }
                        }
                    }
                )
            }

            composable<Main> {
                MainScreen(onNavigateToDetail = { owner, repo ->
                    navController.navigate(Detail(owner, repo))
                })
            }

            composable<Detail> { backStackEntry ->
                val detailArgs = backStackEntry.toRoute<Detail>()
                DetailScreen(
                    owner = detailArgs.owner,
                    repo = detailArgs.repo,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}