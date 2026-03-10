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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.igorergin.ktsandroid.core.designsystem.components.AppTextField
import com.github.igorergin.ktsandroid.feature.repositories.presentation.components.RepositoryCard

@Composable
fun MainScreen(
    onNavigateToDetail: (owner: String, repo: String) -> Unit,
    viewModel: MainViewModel = viewModel { MainViewModel() }
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            Box(modifier = Modifier.padding(16.dp)) {
                AppTextField(value = state.query, onValueChange = viewModel::onSearchQueryChanged, label = "Поиск...", trailingIcon = { Icon(Icons.Default.Search, null) })
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            if (state.isLoading) CircularProgressIndicator()
            else {
                LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    itemsIndexed(state.repositories, key = { _, r -> r.id }) { index, repo ->
                        RepositoryCard(repo) { onNavigateToDetail(repo.ownerName, repo.name) }
                        if (index == state.repositories.lastIndex) LaunchedEffect(Unit) { viewModel.loadNextPage() }
                    }
                    if (state.isPaginating) item { Box(Modifier.fillMaxWidth().padding(8.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator(modifier = Modifier.size(24.dp)) } }
                }
            }
        }
    }
}