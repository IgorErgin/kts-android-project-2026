package com.github.igorergin.ktsandroid.feature.repositories.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
    onNavigateToProfile: () -> Unit,
    viewModel: MainViewModel,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    MainContent(
        state = state,
        onNavigateToDetail = onNavigateToDetail,
        onNavigateToProfile = onNavigateToProfile,
        onQueryChange = viewModel::onSearchQueryChanged,
        onLoadNextPage = viewModel::loadNextPage,
        onRefresh = viewModel::forceRefresh
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent(
    state: MainUiState,
    onNavigateToDetail: (owner: String, repo: String) -> Unit,
    onNavigateToProfile: () -> Unit,
    onQueryChange: (String) -> Unit,
    onLoadNextPage: () -> Unit,
    onRefresh: () -> Unit
) {
    Scaffold(
        topBar = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    AppTextField(
                        value = state.query,
                        onValueChange = onQueryChange,
                        label = "Поиск репозиториев...",
                        trailingIcon = { Icon(Icons.Default.Search, null) }
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = onNavigateToProfile) {
                    Icon(Icons.Default.Person, contentDescription = "Профиль")
                }
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
                    PullToRefreshBox(
                        isRefreshing = state.isRefreshing,
                        onRefresh = onRefresh,
                        modifier = Modifier.fillMaxSize()
                    ) {
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

                                if (index == state.repositories.lastIndex && !state.isLoading && !state.isPaginating && !state.isRefreshing) {
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
}


@Preview
@Composable
private fun MainScreenSuccessPreview() {
    AppTheme {
        MainContent(
            state = MainUiState(
                query = "Kotlin",
                repositories = listOf(
                    GithubRepository(
                        1,
                        "Kotlin",
                        "JetBrains/Kotlin",
                        "The Kotlin Programming Language",
                        "Kotlin",
                        500,
                        "JetBrains",
                        ""
                    ),
                    GithubRepository(
                        2,
                        "Compose",
                        "JetBrains/Compose",
                        "Compose Multiplatform UI framework",
                        "Kotlin",
                        500,
                        "JetBrains",
                        ""
                    )
                )
            ),
            onNavigateToDetail = { _, _ -> },
            onNavigateToProfile = {}, // Заглушка
            onQueryChange = {},
            onLoadNextPage = {},
            onRefresh = {}
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
            onNavigateToProfile = {}, // Заглушка
            onQueryChange = {},
            onLoadNextPage = {},
            onRefresh = {}
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
                    GithubRepository(
                        1,
                        "Kotlin",
                        "JetBrains/Kotlin",
                        "...",
                        "kotlin",
                        500,
                        "JetBrains",
                        ""
                    )
                ),
                isPaginating = true
            ),
            onNavigateToDetail = { _, _ -> },
            onNavigateToProfile = {}, // Заглушка
            onQueryChange = {},
            onLoadNextPage = {},
            onRefresh = {}
        )
    }
}