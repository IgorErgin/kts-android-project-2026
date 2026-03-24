package com.github.igorergin.ktsandroid.feature.repositories.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.igorergin.ktsandroid.core.designsystem.common.AppTextField
import com.github.igorergin.ktsandroid.core.designsystem.theme.AppTheme
import com.github.igorergin.ktsandroid.feature.repositories.domain.model.GithubRepository
import com.github.igorergin.ktsandroid.feature.repositories.presentation.components.RepositoryCard
import ktsandroidproject.composeapp.generated.resources.Res
import ktsandroidproject.composeapp.generated.resources.search_hint
import org.jetbrains.compose.resources.stringResource

@Composable
fun MainScreen(
    onNavigateToDetail: (owner: String, repo: String) -> Unit,
    onNavigateToProfile: () -> Unit,
    viewModel: MainViewModel,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    MainContent(
        state = state,
        onIntent = viewModel::handleIntent,
        onNavigateToDetail = onNavigateToDetail
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent(
    state: MainUiState,
    onIntent: (MainIntent) -> Unit,
    onNavigateToDetail: (owner: String, repo: String) -> Unit
) {
    Scaffold(
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
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                state.isLoading && state.repositories.isEmpty() -> {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }
                state.error != null && state.repositories.isEmpty() -> {
                    Text(state.error, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
                }
                else -> {
                    PullToRefreshBox(
                        isRefreshing = state.isRefreshing,
                        onRefresh = { onIntent(MainIntent.Refresh) },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            itemsIndexed(state.repositories, key = { _, r -> r.id }) { index, repo ->
                                RepositoryCard(
                                    repo = repo,
                                    onClick = { onNavigateToDetail(repo.ownerName, repo.name) },
                                    onFavoriteClick = { onIntent(MainIntent.ToggleFavorite(repo)) }
                                )

                                // Пагинация
                                if (index == state.repositories.lastIndex) {
                                    LaunchedEffect(Unit) {
                                        onIntent(MainIntent.LoadNextPage)
                                    }
                                }
                            }

                            if (state.isPaginating) {
                                item {
                                    Box(Modifier.fillMaxWidth().padding(8.dp), contentAlignment = Alignment.Center) {
                                        CircularProgressIndicator(Modifier.size(24.dp))
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
                    GithubRepository(1, "Kotlin", "JetBrains/Kotlin", "Language", "Kotlin", 1000, "JetBrains", ""),
                    GithubRepository(2, "Compose", "JetBrains/Compose", "UI kit", "Kotlin", 500, "JetBrains", "")
                )
            ),
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
            state = MainUiState(error = "Ошибка подключения к сети"),
            onIntent = {},
            onNavigateToDetail = { _, _ -> }
        )
    }
}