package com.github.igorergin.ktsandroid

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.ktor3.KtorNetworkFetcherFactory
import com.github.igorergin.ktsandroid.core.designsystem.theme.AppTheme
import com.github.igorergin.ktsandroid.core.navigation.Destination
import com.github.igorergin.ktsandroid.core.network.GithubAuthConfig
import com.github.igorergin.ktsandroid.feature.auth.presentation.LoginScreen
import com.github.igorergin.ktsandroid.feature.auth.presentation.LoginViewModel
import com.github.igorergin.ktsandroid.feature.detail.presentation.DetailScreen
import com.github.igorergin.ktsandroid.feature.detail.presentation.DetailViewModel
import com.github.igorergin.ktsandroid.feature.onboarding.presentation.WelcomeScreen
import com.github.igorergin.ktsandroid.feature.repositories.presentation.MainScreen
import com.github.igorergin.ktsandroid.feature.repositories.presentation.MainViewModel


@Composable
fun App() {
    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context).components { add(KtorNetworkFetcherFactory()) }.build()
    }
    val navController = rememberNavController()

    AppTheme {
        NavHost(navController = navController, startDestination = Destination.Welcome) {
            composable<Destination.Welcome> { WelcomeScreen { navController.navigate(
                Destination.Login
            ) } }

            composable<Destination.Login>(
                deepLinks = listOf(navDeepLink<Destination.Login>(basePath = GithubAuthConfig.REDIRECT_URI))
            ) { backStackEntry ->
                val vm: LoginViewModel = viewModel { LoginViewModel() }
                LoginScreen(
                    loginViewModel = vm,
                    onNavigateToMain = {
                        navController.navigate(Destination.Main) {
                            popUpTo(Destination.Welcome) { inclusive = true }
                        }
                    }
                )
            }

            composable<Destination.Main> {
                val vm: MainViewModel = viewModel { MainViewModel() }
                MainScreen(
                    viewModel = vm,
                    onNavigateToDetail = { owner, repo ->
                        navController.navigate(Destination.Detail(owner, repo))
                    })
            }

            composable<Destination.Detail> { backStackEntry ->
                val detailArgs = backStackEntry.toRoute<Destination.Detail>()
                val vm: DetailViewModel =
                    viewModel { DetailViewModel(detailArgs.owner, detailArgs.repo) }
                DetailScreen(
                    repoNameTitle = detailArgs.repo,
                    vm = vm,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}