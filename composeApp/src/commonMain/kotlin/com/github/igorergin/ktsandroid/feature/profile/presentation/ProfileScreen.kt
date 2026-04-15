package com.github.igorergin.ktsandroid.feature.profile.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.github.igorergin.ktsandroid.core.designsystem.theme.AppTheme
import com.github.igorergin.ktsandroid.core.designsystem.theme.GitHubTextSecondary
import com.github.igorergin.ktsandroid.feature.profile.domain.model.GithubEvent
import com.github.igorergin.ktsandroid.feature.profile.domain.model.UserProfile
import com.github.igorergin.ktsandroid.feature.repositories.domain.model.GithubRepository
import ktsandroidproject.composeapp.generated.resources.Res
import ktsandroidproject.composeapp.generated.resources.error_prefix
import ktsandroidproject.composeapp.generated.resources.profile_logout
import ktsandroidproject.composeapp.generated.resources.tab_profile
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onRepoClick: (owner: String, name: String) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ProfileContentWrapper(
        state = state,
        onBack = onBack,
        onTabSelected = viewModel::onTabSelected,
        onLogout = { viewModel.logout(onLogoutComplete = onNavigateToLogin) },
        onRepoClick = onRepoClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileContentWrapper(
    state: ProfileUiState,
    onBack: () -> Unit,
    onTabSelected: (Int) -> Unit,
    onLogout: () -> Unit,
    onRepoClick: (owner: String, name: String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.tab_profile)) },
                navigationIcon = {
                    if (onBack != {}) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(
                            Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = stringResource(Res.string.profile_logout),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                state.error != null -> {
                    Text(
                        text = "${stringResource(Res.string.error_prefix)}${state.error}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center).padding(16.dp)
                    )
                }
                state.profile != null -> {
                    ProfileContent(
                        profile = state.profile,
                        repos = state.repos,
                        events = state.events,
                        selectedTab = state.selectedTab,
                        isActivityLoading = state.isActivityLoading,
                        onTabSelected = onTabSelected,
                        onRepoClick = { repo ->
                            onRepoClick(repo.ownerName, repo.name)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileContent(
    profile: UserProfile,
    repos: List<GithubRepository>,
    events: List<GithubEvent>,
    selectedTab: Int,
    isActivityLoading: Boolean,
    onTabSelected: (Int) -> Unit,
    onRepoClick: (GithubRepository) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // Хедер профиля
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp, horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = profile.avatarUrl,
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = profile.name ?: profile.login,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "@${profile.login}",
                    style = MaterialTheme.typography.titleMedium,
                    color = GitHubTextSecondary
                )
                
                if (!profile.bio.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = profile.bio,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatBox(profile.followersCount.toString(), "Followers")
                    StatBox(profile.publicReposCount.toString(), "Repositories")
                }
            }
        }

        // Вкладки
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                divider = {}
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { onTabSelected(0) },
                    text = { Text("Repositories", fontWeight = FontWeight.SemiBold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { onTabSelected(1) },
                    text = { Text("Activity", fontWeight = FontWeight.SemiBold) }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Контент вкладок
        if (selectedTab == 0) {
            if (repos.isEmpty()) {
                item {
                    Box(Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
                        Text("No repositories found", color = GitHubTextSecondary)
                    }
                }
            } else {
                items(repos) { repo ->
                    RepositoryCard(repo, onRepoClick)
                }
            }
        } else {
            if (isActivityLoading && events.isEmpty()) {
                item {
                    Box(Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            } else {
                items(events) { event ->
                    EventItem(event)
                }
            }
        }
    }
}

@Composable
private fun StatBox(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value, 
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = label, 
            style = MaterialTheme.typography.labelMedium,
            color = GitHubTextSecondary
        )
    }
}

@Composable
private fun RepositoryCard(repo: GithubRepository, onClick: (GithubRepository) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        onClick = { onClick(repo) }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = repo.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                if (repo.language.isNotBlank()) {
                    LanguageBadge(repo.language)
                }
            }
            
            if (repo.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = repo.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Color(0xFFFFB300)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = repo.starsCount.toString(),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Updated recently",
                    style = MaterialTheme.typography.labelSmall,
                    color = GitHubTextSecondary
                )
            }
        }
    }
}

@Composable
private fun LanguageBadge(language: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondary)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = language,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun EventItem(event: GithubEvent) {
    ListItem(
        headlineContent = { Text(event.type.replace("Event", ""), fontWeight = FontWeight.Bold) },
        supportingContent = {
            Column {
                Text(event.repoName, color = MaterialTheme.colorScheme.primary)
                if (event.commitMessages.isNotEmpty()) {
                    Text(
                        event.commitMessages.first(),
                        maxLines = 1,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        },
        overlineContent = { Text(event.createdAt.substringBefore("T")) },
        leadingContent = {
            Box(
                modifier = Modifier.size(8.dp).clip(CircleShape).background(MaterialTheme.colorScheme.outline)
            )
        }
    )
}

@Preview
@Composable
private fun ProfileSuccessPreview() {
    AppTheme {
        ProfileContentWrapper(
            state = ProfileUiState(
                profile = UserProfile(
                    id = "1",
                    login = "igorergin",
                    name = "Igor Ergin",
                    avatarUrl = "",
                    bio = "KMP Developer",
                    followersCount = 100,
                    publicReposCount = 50
                ),
                repos = emptyList(),
                isLoading = false
            ),
            onBack = {},
            onTabSelected = {},
            onLogout = {},
            onRepoClick = { _, _ -> }
        )
    }
}
