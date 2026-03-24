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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.github.igorergin.ktsandroid.core.designsystem.theme.AppTheme
import com.github.igorergin.ktsandroid.core.designsystem.theme.GitHubTextSecondary
import com.github.igorergin.ktsandroid.feature.detail.domain.model.RepositoryDetail
import ktsandroidproject.composeapp.generated.resources.Res
import ktsandroidproject.composeapp.generated.resources.create_issue
import ktsandroidproject.composeapp.generated.resources.decline
import ktsandroidproject.composeapp.generated.resources.description
import ktsandroidproject.composeapp.generated.resources.details_language
import ktsandroidproject.composeapp.generated.resources.error_prefix
import ktsandroidproject.composeapp.generated.resources.header
import ktsandroidproject.composeapp.generated.resources.no_data
import ktsandroidproject.composeapp.generated.resources.readme_title
import ktsandroidproject.composeapp.generated.resources.retry
import ktsandroidproject.composeapp.generated.resources.stars_count
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
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
        onRetry = vm::loadAll,
        onShare = vm::onShareClick,
        onAddIssueClick = { vm.setIssueDialogVisible(true) }
    )

    if (state.isIssueDialogVisible) {
        CreateIssueDialog(
            isSending = state.isIssueSending,
            error = state.issueError,
            onDismiss = { vm.setIssueDialogVisible(false) },
            onConfirm = vm::createIssue
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailContent(
    repoNameTitle: String,
    state: DetailUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onShare: () -> Unit,
    onAddIssueClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(repoNameTitle) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(onClick = onShare) {
                        Icon(Icons.Default.Share, contentDescription = "Поделиться")
                    }
                    IconButton(onClick = onAddIssueClick) {
                        Icon(Icons.Default.AddComment, contentDescription = "Создать Issue")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                state.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                state.error != null -> {
                    Column(
                        Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "${stringResource(Res.string.error_prefix)}${state.error}",
                            color = MaterialTheme.colorScheme.error
                        )
                        Button(onClick = onRetry) { Text(stringResource(Res.string.retry)) }
                    }
                }

                state.repository != null -> {
                    RepositoryInfo(
                        repo = state.repository,
                        readme = state.readme,
                        isReadmeLoading = state.isReadmeLoading
                    )
                }
            }
        }
    }
}

@Composable
fun RepositoryInfo(repo: RepositoryDetail, readme: String?, isReadmeLoading: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = repo.ownerAvatarUrl,
            contentDescription = null,
            modifier = Modifier.size(100.dp).clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(repo.ownerLogin, color = GitHubTextSecondary)
        Text(
            repo.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Row(
                Modifier.padding(16.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                StatView(stringResource(Res.string.stars_count), repo.starsCount.toString(), Icons.Default.Star)
                StatView(stringResource(Res.string.details_language), repo.language ?: "N/A", Icons.Default.Code)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            stringResource(Res.string.readme_title),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.fillMaxWidth()
        )
        Divider(Modifier.padding(vertical = 8.dp))

        if (isReadmeLoading) {
            CircularProgressIndicator(modifier = Modifier.size(30.dp).padding(top = 16.dp))
        } else {
            Text(
                text = readme ?: stringResource(Res.string.no_data),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun CreateIssueDialog(
    isSending: Boolean,
    error: String?,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.create_issue)) },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(stringResource(Res.string.header)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = body,
                    onValueChange = { body = it },
                    label = { Text(stringResource(Res.string.description)) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
                if (error != null) {
                    Text(
                        error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(title, body) },
                enabled = !isSending && title.isNotBlank()
            ) {
                if (isSending) CircularProgressIndicator(Modifier.size(18.dp)) else Text("Создать")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(Res.string.decline)) } }
    )
}

@Composable
private fun StatView(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Text(value, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelSmall, color = GitHubTextSecondary)
    }
}

@Preview
@Composable
private fun DetailPreview() {
    AppTheme {
        DetailContent(
            repoNameTitle = "kts-android",
            state = DetailUiState(
                repository = RepositoryDetail(
                    1,
                    "kts-android",
                    "igorergin/kts-android",
                    "Desc",
                    42,
                    5,
                    2,
                    "igorergin",
                    "",
                    "url",
                    "Kotlin"
                ),
                readme = "Это превью README контента..."
            ),
            onBack = {}, onRetry = {}, onShare = {}, onAddIssueClick = {}
        )
    }
}