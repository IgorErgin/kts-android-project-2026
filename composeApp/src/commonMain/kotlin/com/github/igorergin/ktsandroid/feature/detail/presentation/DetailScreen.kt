package com.github.igorergin.ktsandroid.feature.detail.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.SubcomposeAsyncImage
import com.github.igorergin.ktsandroid.core.designsystem.common.AppErrorState
import com.github.igorergin.ktsandroid.core.designsystem.theme.AppTheme
import com.github.igorergin.ktsandroid.core.designsystem.theme.GitHubTextSecondary
import com.github.igorergin.ktsandroid.feature.detail.domain.model.RepositoryDetail

@Composable
fun DetailScreen(
    repoNameTitle: String,
    onBack: () -> Unit,
    vm: DetailViewModel
) {
    val state by vm.state.collectAsStateWithLifecycle()

    DetailContent(
        repoNameTitle = repoNameTitle,
        state = state,
        onBack = onBack,
        onRetry = vm::loadRepository
    )
}

/**
 * Stateless-версия: только верстка.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailContent(
    repoNameTitle: String,
    state: DetailUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(repoNameTitle, maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }

                state.error != null -> {
                    AppErrorState(
                        message = state.error,
                        onRetry = onRetry,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    state.repository?.let { repository ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(horizontal = 24.dp, vertical = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            SubcomposeAsyncImage(
                                model = repository.ownerAvatarUrl,
                                contentDescription = "Owner Avatar",
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                loading = {
                                    CircularProgressIndicator(modifier = Modifier.padding(32.dp))
                                },
                                error = {
                                    Icon(
                                        Icons.Default.Error,
                                        contentDescription = "Error",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                repository.ownerLogin,
                                style = MaterialTheme.typography.titleMedium,
                                color = GitHubTextSecondary
                            )
                            Text(
                                repository.name,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    StatItem("Stars", repository.starsCount.toString(), Icons.Default.Star)
                                    StatItem("Language", repository.language ?: "N/A", null)
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            repository.description?.let {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector?
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(
                    icon,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
        Text(label, style = MaterialTheme.typography.bodyMedium, color = GitHubTextSecondary)
    }
}


@Preview
@Composable
private fun DetailScreenSuccessPreview() {
    AppTheme {
        DetailContent(
            repoNameTitle = "Compose Multiplatform",
            state = DetailUiState(
                repository = RepositoryDetail(
                    id = 1,
                    name = "compose-multiplatform",
                    fullName = "JetBrains/compose-multiplatform",
                    description = "Compose Multiplatform, a modern UI framework for Kotlin that announces a new era of UI development.",
                    starsCount = 15400,
                    forksCount = 1200,
                    openIssuesCount = 45,
                    ownerLogin = "JetBrains",
                    ownerAvatarUrl = "",
                    language = "Kotlin",
                    htmlUrl = ""
                )
            ),
            onBack = {},
            onRetry = {}
        )
    }
}

@Preview
@Composable
private fun DetailScreenLoadingPreview() {
    AppTheme {
        DetailContent(
            repoNameTitle = "Загрузка...",
            state = DetailUiState(isLoading = true),
            onBack = {},
            onRetry = {}
        )
    }
}

@Preview
@Composable
private fun DetailScreenErrorPreview() {
    AppTheme {
        DetailContent(
            repoNameTitle = "Ошибка",
            state = DetailUiState(error = "Не удалось загрузить данные. Проверьте соединение."),
            onBack = {},
            onRetry = {}
        )
    }
}