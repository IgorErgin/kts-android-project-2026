package com.github.igorergin.ktsandroid

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.ktor3.KtorNetworkFetcherFactory
import com.github.igorergin.ktsandroid.core.datastore.AppSettings
import com.github.igorergin.ktsandroid.core.designsystem.theme.AppTheme
import com.github.igorergin.ktsandroid.core.navigation.Destination
import com.github.igorergin.ktsandroid.core.network.GithubAuthConfig
import com.github.igorergin.ktsandroid.feature.auth.presentation.LoginScreen
import com.github.igorergin.ktsandroid.feature.auth.presentation.LoginViewModel
import com.github.igorergin.ktsandroid.feature.detail.presentation.DetailScreen
import com.github.igorergin.ktsandroid.feature.detail.presentation.DetailViewModel
import com.github.igorergin.ktsandroid.feature.main.MainContainerScreen
import com.github.igorergin.ktsandroid.feature.onboarding.presentation.WelcomeScreen
import com.github.igorergin.ktsandroid.feature.splash.presentation.SplashScreen
import com.github.igorergin.ktsandroid.feature.splash.presentation.SplashViewModel
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun App() {
    val appSettings: AppSettings = koinInject()

    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context)
            .components { add(KtorNetworkFetcherFactory()) }
            .build()
    }

    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

    AppTheme {
        NavHost(navController = navController, startDestination = Destination.Splash) {

            composable<Destination.Splash> {
                val vm: SplashViewModel = koinViewModel()
                SplashScreen(
                    viewModel = vm,
                    onNavigate = { dest ->
                        navController.navigate(dest) {
                            popUpTo(Destination.Splash) { inclusive = true }
                        }
                    }
                )
            }

            composable<Destination.Welcome> {
                WelcomeScreen(
                    onNavigateToLogin = {
                        scope.launch { appSettings.setFirstLaunch(false) }
                        navController.navigate(Destination.Login) {
                            popUpTo(Destination.Welcome) { inclusive = true }
                        }
                    }
                )
            }

            composable<Destination.Login>(
                deepLinks = listOf(navDeepLink<Destination.Login>(basePath = GithubAuthConfig.REDIRECT_URI))
            ) {
                val vm: LoginViewModel = koinViewModel()
                LoginScreen(
                    loginViewModel = vm,
                    onNavigateToMain = {
                        navController.navigate(Destination.MainContainer) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable<Destination.MainContainer> {
                MainContainerScreen(
                    onNavigateToDetail = { owner, repo ->
                        navController.navigate(Destination.Detail(owner, repo))
                    },
                    onNavigateToLogin = {
                        navController.navigate(Destination.Login) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable<Destination.Detail> { backStackEntry ->
                val detailArgs = backStackEntry.toRoute<Destination.Detail>()
                val vm: DetailViewModel = koinViewModel(
                    parameters = { parametersOf(detailArgs.owner, detailArgs.repo) }
                )
                DetailScreen(
                    repoNameTitle = detailArgs.repo,
                    vm = vm,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}