package com.github.igorergin.ktsandroid.feature.repositories.presentation.favorites

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.igorergin.ktsandroid.core.designsystem.theme.AppTheme
import com.github.igorergin.ktsandroid.feature.repositories.domain.model.GithubRepository
import com.github.igorergin.ktsandroid.feature.repositories.presentation.components.RepositoryCard
import ktsandroidproject.composeapp.generated.resources.Res
import ktsandroidproject.composeapp.generated.resources.no_favorites
import ktsandroidproject.composeapp.generated.resources.tab_favorites
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel,
    onNavigateToDetail: (String, String) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    FavoritesContent(
        state = state,
        onToggleFavorite = viewModel::toggleFavorite,
        onNavigateToDetail = onNavigateToDetail
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesContent(
    state: FavoritesUiState,
    onToggleFavorite: (GithubRepository) -> Unit,
    onNavigateToDetail: (String, String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(Res.string.tab_favorites)) })
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                state.isLoading && state.favorites.isEmpty() -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                state.favorites.isEmpty() -> {
                    Text(
                        text = stringResource(Res.string.no_favorites),
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.favorites, key = { it.id }) { repo ->
                            RepositoryCard(
                                repo = repo,
                                isFavorite = true,
                                onFavoriteClick = { onToggleFavorite(repo) },
                                onClick = { onNavigateToDetail(repo.ownerName, repo.name) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun FavoritesEmptyPreview() {
    AppTheme {
        FavoritesContent(
            state = FavoritesUiState(favorites = emptyList()),
            onToggleFavorite = {},
            onNavigateToDetail = { _, _ -> }
        )
    }
}

@Preview
@Composable
private fun FavoritesListPreview() {
    AppTheme {
        FavoritesContent(
            state = FavoritesUiState(
                favorites = listOf(
                    GithubRepository(1, "Repo 1", "Owner/Repo 1", "Desc", "Kotlin", 10, "Owner", ""),
                    GithubRepository(2, "Repo 2", "Owner/Repo 2", "Desc", "Java", 20, "Owner", "")
                )
            ),
            onToggleFavorite = {},
            onNavigateToDetail = { _, _ -> }
        )
    }
}