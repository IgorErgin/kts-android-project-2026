package com.github.igorergin.ktsandroid.feature.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.github.igorergin.ktsandroid.core.designsystem.theme.AppTheme
import com.github.igorergin.ktsandroid.core.navigation.Destination
import com.github.igorergin.ktsandroid.core.util.SnackbarManager
import com.github.igorergin.ktsandroid.feature.repositories.presentation.MainScreen
import com.github.igorergin.ktsandroid.feature.profile.presentation.ProfileScreen
import com.github.igorergin.ktsandroid.feature.repositories.presentation.favorites.FavoritesScreen
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MainContainerScreen(
    onNavigateToDetail: (String, String) -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        SnackbarManager.messages.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    MainContainerContent(
        navController = navController,
        currentDestination = currentDestination,
        snackbarHostState = snackbarHostState,
        onNavigateToDetail = onNavigateToDetail,
        onNavigateToLogin = onNavigateToLogin
    )
}

@Composable
fun MainContainerContent(
    navController: NavHostController,
    currentDestination: NavDestination?,
    snackbarHostState: SnackbarHostState,
    onNavigateToDetail: (String, String) -> Unit,
    onNavigateToLogin: () -> Unit
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentDestination?.hierarchy?.any { it.hasRoute<Destination.SearchTab>() } == true,
                    onClick = {
                        navController.navigate(Destination.SearchTab) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = { Icon(Icons.Default.Search, contentDescription = null) },
                    label = { Text("Поиск") }
                )
                NavigationBarItem(
                    selected = currentDestination?.hierarchy?.any { it.hasRoute<Destination.FavoritesTab>() } == true,
                    onClick = {
                        navController.navigate(Destination.FavoritesTab) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = { Icon(Icons.Default.Favorite, contentDescription = null) },
                    label = { Text("Избранное") }
                )
                NavigationBarItem(
                    selected = currentDestination?.hierarchy?.any { it.hasRoute<Destination.ProfileTab>() } == true,
                    onClick = {
                        navController.navigate(Destination.ProfileTab) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text("Профиль") }
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Destination.SearchTab,
            modifier = Modifier.padding(padding)
        ) {
            composable<Destination.SearchTab> {
                MainScreen(
                    viewModel = koinViewModel(),
                    onNavigateToDetail = onNavigateToDetail,
                    onNavigateToProfile = { /* Не требуется в BottomBar */ }
                )
            }
            composable<Destination.FavoritesTab> {
                FavoritesScreen(
                    viewModel = koinViewModel(),
                    onNavigateToDetail = onNavigateToDetail
                )
            }
            composable<Destination.ProfileTab> {
                ProfileScreen(
                    viewModel = koinViewModel(),
                    onBack = { /* Не требуется в табе */ },
                    onNavigateToLogin = onNavigateToLogin,
                    onRepoClick = onNavigateToDetail
                )
            }
        }
    }
}

@Preview
@Composable
private fun MainContainerPreview() {
    AppTheme {
        MainContainerContent(
            navController = rememberNavController(),
            currentDestination = null,
            snackbarHostState = remember { SnackbarHostState() },
            onNavigateToDetail = { _, _ -> },
            onNavigateToLogin = {}
        )
    }
}

@Preview
@Composable
private fun MainContainerDarkPreview() {
    AppTheme(darkTheme = true) {
        MainContainerContent(
            navController = rememberNavController(),
            currentDestination = null,
            snackbarHostState = remember { SnackbarHostState() },
            onNavigateToDetail = { _, _ -> },
            onNavigateToLogin = {}
        )
    }
}