package com.github.igorergin.ktsandroid.feature.repositories.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.igorergin.ktsandroid.core.designsystem.common.AppTextField
import com.github.igorergin.ktsandroid.core.designsystem.theme.AppTheme
import com.github.igorergin.ktsandroid.core.domain.error.AppError
import com.github.igorergin.ktsandroid.feature.repositories.domain.model.GithubRepository
import com.github.igorergin.ktsandroid.feature.repositories.domain.model.RepositoryId
import com.github.igorergin.ktsandroid.feature.repositories.presentation.components.RepositoryCard
import ktsandroidproject.composeapp.generated.resources.Res
import ktsandroidproject.composeapp.generated.resources.error_network
import ktsandroidproject.composeapp.generated.resources.error_server
import ktsandroidproject.composeapp.generated.resources.error_unknown
import ktsandroidproject.composeapp.generated.resources.offline_mode
import ktsandroidproject.composeapp.generated.resources.search_hint
import org.jetbrains.compose.resources.stringResource

@Composable
fun MainScreen(
    onNavigateToDetail: (owner: String, repo: String) -> Unit,
    onNavigateToProfile: () -> Unit,
    viewModel: MainViewModel,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    val networkErrorMessage = stringResource(Res.string.error_network)
    val serverErrorMessage = stringResource(Res.string.error_server)
    val unknownErrorMessage = stringResource(Res.string.error_unknown)

    LaunchedEffect(viewModel.sideEffect) {
        viewModel.sideEffect.collect { sideEffect ->
            when (sideEffect) {
                is MainSideEffect.Error -> {
                    val message = when (val error = sideEffect.error) {
                        AppError.Network -> networkErrorMessage
                        AppError.Server -> serverErrorMessage
                        AppError.Unauthorized -> unknownErrorMessage
                        is AppError.Unknown -> error.message ?: unknownErrorMessage
                    }
                    snackbarHostState.showSnackbar(message)
                }
            }
        }
    }

    MainContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onIntent = viewModel::handleIntent,
        onNavigateToDetail = onNavigateToDetail
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent(
    state: MainUiState,
    snackbarHostState: SnackbarHostState,
    onIntent: (MainIntent) -> Unit,
    onNavigateToDetail: (owner: String, repo: String) -> Unit
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                AppTextField(
                    value = state.query,
                    onValueChange = { onIntent(MainIntent.SearchQueryChanged(it)) },
                    label = stringResource(Res.string.search_hint),
                    trailingIcon = { Icon(Icons.Default.Search, null) }
                )
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (state.isOfflineData) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.tertiaryContainer)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(Res.string.offline_mode),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    state.isLoading && state.repositories.isEmpty() -> {
                        CircularProgressIndicator(Modifier.align(Alignment.Center))
                    }
                    state.error != null && state.repositories.isEmpty() -> {
                        val errorMessage = when (val err = state.error) {
                            AppError.Network -> stringResource(Res.string.error_network)
                            AppError.Server -> stringResource(Res.string.error_server)
                            is AppError.Unknown -> err.message ?: stringResource(Res.string.error_unknown)
                            AppError.Unauthorized -> stringResource(Res.string.error_unknown)
                            else -> ""
                        }
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    else -> {
                        PullToRefreshBox(
                            isRefreshing = state.isRefreshing,
                            onRefresh = { onIntent(MainIntent.Refresh) },
                            modifier = Modifier.fillMaxSize()
                        ) {
                            val listState = rememberLazyListState()
                            
                            val shouldLoadNextPage = remember {
                                derivedStateOf {
                                    val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
                                    lastVisibleItem != null && 
                                            lastVisibleItem.index >= state.repositories.size - 2 &&
                                            !state.isPaginating &&
                                            state.error == null
                                }
                            }

                            LaunchedEffect(shouldLoadNextPage.value) {
                                if (shouldLoadNextPage.value) {
                                    onIntent(MainIntent.LoadNextPage)
                                }
                            }

                            LazyColumn(
                                state = listState,
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                itemsIndexed(state.repositories, key = { _, r -> r.id.value }) { index, repo ->
                                    RepositoryCard(
                                        repo = repo,
                                        isFavorite = repo.isFavorite,
                                        onClick = { onNavigateToDetail(repo.ownerName, repo.name) },
                                        onFavoriteClick = { onIntent(MainIntent.ToggleFavorite(repo)) }
                                    )
                                }

                                if (state.isPaginating) {
                                    item {
                                        Box(Modifier.fillMaxWidth().padding(8.dp), contentAlignment = Alignment.Center) {
                                            CircularProgressIndicator(Modifier.size(24.dp))
                                        }
                                    }
                                }
                                
                                if (state.error != null && state.repositories.isNotEmpty()) {
                                    item {
                                        val errorMessage = when (val err = state.error) {
                                            AppError.Network -> stringResource(Res.string.error_network)
                                            AppError.Server -> stringResource(Res.string.error_server)
                                            is AppError.Unknown -> err.message ?: stringResource(Res.string.error_unknown)
                                            AppError.Unauthorized -> stringResource(Res.string.error_unknown)
                                            else -> ""
                                        }
                                        Text(
                                            text = errorMessage,
                                            color = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun MainSuccessPreview() {
    AppTheme {
        MainContent(
            state = MainUiState(
                repositories = listOf(
                    GithubRepository(RepositoryId(1), "Kotlin", "JetBrains/Kotlin", "Language", "Kotlin", 1000, "JetBrains", "", false),
                    GithubRepository(RepositoryId(2), "Compose", "JetBrains/Compose", "UI kit", "Kotlin", 500, "JetBrains", "", true)
                )
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onIntent = {},
            onNavigateToDetail = { _, _ -> }
        )
    }
}

@Preview
@Composable
private fun MainLoadingPreview() {
    AppTheme(darkTheme = true) {
        MainContent(
            state = MainUiState(isLoading = true),
            snackbarHostState = remember { SnackbarHostState() },
            onIntent = {},
            onNavigateToDetail = { _, _ -> }
        )
    }
}

@Preview
@Composable
private fun MainErrorPreview() {
    AppTheme {
        MainContent(
            state = MainUiState(error = AppError.Network),
            snackbarHostState = remember { SnackbarHostState() },
            onIntent = {},
            onNavigateToDetail = { _, _ -> }
        )
    }
}
