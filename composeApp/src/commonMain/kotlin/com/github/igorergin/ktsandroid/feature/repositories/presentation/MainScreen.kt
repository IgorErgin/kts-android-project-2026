package com.github.igorergin.ktsandroid.feature.repositories.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.igorergin.ktsandroid.core.designsystem.common.AppTextField
import com.github.igorergin.ktsandroid.core.designsystem.theme.AppTheme
import com.github.igorergin.ktsandroid.feature.repositories.domain.model.GithubRepository
import com.github.igorergin.ktsandroid.feature.repositories.presentation.components.RepositoryCard

@Composable
fun MainScreen(
    onNavigateToDetail: (owner: String, repo: String) -> Unit,
    viewModel: MainViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    MainContent(
        state = state,
        onNavigateToDetail = onNavigateToDetail,
        onQueryChange = viewModel::onSearchQueryChanged,
        onLoadNextPage = viewModel::loadNextPage
    )
}


@Composable
fun MainContent(
    state: MainUiState,
    onNavigateToDetail: (owner: String, repo: String) -> Unit,
    onQueryChange: (String) -> Unit,
    onLoadNextPage: () -> Unit
) {
    Scaffold(
        topBar = {
            Box(modifier = Modifier.padding(16.dp)) {
                AppTextField(
                    value = state.query,
                    onValueChange = onQueryChange,
                    label = "Поиск репозиториев...",
                    trailingIcon = { Icon(Icons.Default.Search, null) }
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            when {
                state.isLoading && state.repositories.isEmpty() -> {
                    CircularProgressIndicator()
                }
                state.error != null && state.repositories.isEmpty() -> {
                    Text(
                        text = state.error,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                state.repositories.isEmpty() && !state.isLoading -> {
                    Text("Ничего не найдено", style = MaterialTheme.typography.bodyLarge)
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        itemsIndexed(
                            items = state.repositories,
                            key = { _, r -> r.id }
                        ) { index, repo ->
                            RepositoryCard(repo) {
                                onNavigateToDetail(repo.ownerName, repo.name)
                            }

                            if (index == state.repositories.lastIndex && !state.isLoading) {
                                LaunchedEffect(Unit) { onLoadNextPage() }
                            }
                        }

                        if (state.isPaginating) {
                            item {
                                Box(
                                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
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
private fun MainScreenSuccessPreview() {
    AppTheme {
        MainContent(
            state = MainUiState(
                query = "Kotlin",
                repositories = listOf(
                    GithubRepository(1, "Kotlin", "JetBrains/Kotlin", "The Kotlin Programming Language", "Kotlin", 500, "JetBrains", ""),
                    GithubRepository(2, "Compose", "JetBrains/Compose", "Compose Multiplatform UI framework", "Kotlin", 500, "JetBrains", "")
                )
            ),
            onNavigateToDetail = { _, _ -> },
            onQueryChange = {},
            onLoadNextPage = {}
        )
    }
}

@Preview
@Composable
private fun MainScreenLoadingPreview() {
    AppTheme {
        MainContent(
            state = MainUiState(isLoading = true),
            onNavigateToDetail = { _, _ -> },
            onQueryChange = {},
            onLoadNextPage = {}
        )
    }
}

@Preview
@Composable
private fun MainScreenPaginationPreview() {
    AppTheme {
        MainContent(
            state = MainUiState(
                repositories = listOf(
                    GithubRepository(1, "Kotlin", "JetBrains/Kotlin", "...", "kotlin", 500, "JetBrains", "")
                ),
                isPaginating = true
            ),
            onNavigateToDetail = { _, _ -> },
            onQueryChange = {},
            onLoadNextPage = {}
        )
    }
}