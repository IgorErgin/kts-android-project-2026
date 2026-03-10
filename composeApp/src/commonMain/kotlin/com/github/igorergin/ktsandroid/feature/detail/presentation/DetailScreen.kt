package com.github.igorergin.ktsandroid.feature.detail.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.github.igorergin.ktsandroid.core.designsystem.theme.GitHubTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    owner: String,
    repo: String,
    onBack: () -> Unit,
    vm: DetailViewModel = viewModel { DetailViewModel() }
) {
    // Best Practice: collectAsStateWithLifecycle
    val state by vm.state.collectAsStateWithLifecycle()

    LaunchedEffect(owner, repo) {
        vm.loadRepository(owner, repo)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(repo, maxLines = 1) },
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
                state.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                state.error != null -> {
                    Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Ошибка загрузки", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(state.error ?: "", color = GitHubTextSecondary)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { vm.loadRepository(owner, repo) }) { Text("Повторить") }
                    }
                }
                state.repository != null -> {
                    val repository = state.repository!!
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AsyncImage(
                            model = repository.owner.avatarUrl,
                            contentDescription = "Owner Avatar",
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(repository.owner.login, style = MaterialTheme.typography.titleMedium, color = GitHubTextSecondary)
                        Text(repository.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

                        Spacer(modifier = Modifier.height(24.dp))

                        // Карточка статистики
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                StatItem("Stars", repository.stargazersCount.toString(), Icons.Default.Star)
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

@Composable
private fun StatItem(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector?) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
        Text(label, style = MaterialTheme.typography.bodyMedium, color = GitHubTextSecondary)
    }
}