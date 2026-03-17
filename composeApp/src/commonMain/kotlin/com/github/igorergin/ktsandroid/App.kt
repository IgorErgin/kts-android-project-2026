package com.github.igorergin.ktsandroid

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.ktor3.KtorNetworkFetcherFactory
import com.github.igorergin.ktsandroid.core.datastore.AppSettings
import com.github.igorergin.ktsandroid.core.datastore.TokenManager
import com.github.igorergin.ktsandroid.core.designsystem.theme.AppTheme
import com.github.igorergin.ktsandroid.core.navigation.Destination
import com.github.igorergin.ktsandroid.core.network.GithubAuthConfig
import com.github.igorergin.ktsandroid.feature.auth.data.repository.GithubAuthRepository
import com.github.igorergin.ktsandroid.feature.auth.presentation.LoginScreen
import com.github.igorergin.ktsandroid.feature.auth.presentation.LoginViewModel
import com.github.igorergin.ktsandroid.feature.detail.data.repository.DetailRepository
import com.github.igorergin.ktsandroid.feature.detail.presentation.DetailScreen
import com.github.igorergin.ktsandroid.feature.detail.presentation.DetailViewModel
import com.github.igorergin.ktsandroid.feature.onboarding.presentation.WelcomeScreen
import com.github.igorergin.ktsandroid.feature.profile.data.repository.ProfileRepository
import com.github.igorergin.ktsandroid.feature.profile.presentation.ProfileScreen
import com.github.igorergin.ktsandroid.feature.profile.presentation.ProfileViewModel
import com.github.igorergin.ktsandroid.feature.repositories.domain.repository.GithubRepoRepository
import com.github.igorergin.ktsandroid.feature.repositories.presentation.MainScreen
import com.github.igorergin.ktsandroid.feature.repositories.presentation.MainViewModel
import kotlinx.coroutines.launch

@Composable
fun App(
    appSettings: AppSettings,
    tokenManager: TokenManager,
    githubRepoRepository: GithubRepoRepository,
    profileRepository: ProfileRepository,
    githubAuthRepository: GithubAuthRepository,
    detailRepository: DetailRepository
) {
    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context)
            .components { add(KtorNetworkFetcherFactory()) }
            .build()
    }

    val isFirstLaunch by appSettings.isFirstLaunchFlow.collectAsStateWithLifecycle(initialValue = null)
    val accessToken by tokenManager.accessToken.collectAsStateWithLifecycle()

    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

    AppTheme {
        if (isFirstLaunch == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@AppTheme
        }

        val startDestination = when {
            isFirstLaunch == true -> Destination.Welcome
            accessToken != null -> Destination.Main
            else -> Destination.Login
        }

        NavHost(navController = navController, startDestination = startDestination) {

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
                val vm: LoginViewModel = viewModel { LoginViewModel(githubAuthRepository) }
                LoginScreen(
                    loginViewModel = vm,
                    onNavigateToMain = {
                        navController.navigate(Destination.Main) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable<Destination.Main> {
                val vm: MainViewModel = viewModel { MainViewModel(githubRepoRepository) }
                MainScreen(
                    viewModel = vm,
                    onNavigateToDetail = { owner, repo ->
                        navController.navigate(Destination.Detail(owner, repo))
                    },
                    onNavigateToProfile = {
                        navController.navigate(Destination.Profile)
                    }
                )
            }

            composable<Destination.Detail> { backStackEntry ->
                val detailArgs = backStackEntry.toRoute<Destination.Detail>()
                val vm: DetailViewModel = viewModel {
                    DetailViewModel(detailArgs.owner, detailArgs.repo, detailRepository)
                }
                DetailScreen(
                    repoNameTitle = detailArgs.repo,
                    vm = vm,
                    onBack = { navController.popBackStack() }
                )
            }

            composable<Destination.Profile> {
                val vm: ProfileViewModel = viewModel {
                    ProfileViewModel(profileRepository, tokenManager, githubRepoRepository)
                }
                ProfileScreen(
                    viewModel = vm,
                    onBack = { navController.popBackStack() },
                    onNavigateToLogin = {
                        navController.navigate(Destination.Login) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}