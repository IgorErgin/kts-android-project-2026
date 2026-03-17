package com.github.igorergin.ktsandroid.feature.repositories.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.github.igorergin.ktsandroid.core.designsystem.theme.AppTheme
import com.github.igorergin.ktsandroid.core.designsystem.theme.GitHubTextSecondary
import com.github.igorergin.ktsandroid.feature.repositories.domain.model.GithubRepository

@Composable
fun RepositoryCard(
    repo: GithubRepository,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = repo.ownerAvatarUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = repo.fullName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "⭐ ${repo.starsCount}",
                    style = MaterialTheme.typography.labelMedium,
                    color = GitHubTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (repo.description.isNotBlank()) {
                Text(
                    text = repo.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondary)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = repo.language.ifBlank { "Unknown" },
                    style = MaterialTheme.typography.labelSmall,
                    color = GitHubTextSecondary
                )
            }
        }
    }
}


@Preview
@Composable
private fun RepositoryCardPreview() {
    AppTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            RepositoryCard(
                repo = GithubRepository(
                    id = 1,
                    name = "compose-multiplatform",
                    fullName = "JetBrains/compose-multiplatform",
                    description = "Compose Multiplatform, a modern UI framework for Kotlin that announces a new era of UI development.",
                    starsCount = 15400,
                    language = "Kotlin",
                    ownerName = "JetBrains",
                    ownerAvatarUrl = ""
                ),
                onClick = {}
            )
        }
    }
}

@Preview
@Composable
private fun RepositoryCardLongTitlePreview() {
    AppTheme(darkTheme = true) {
        Surface(modifier = Modifier.padding(16.dp)) {
            RepositoryCard(
                repo = GithubRepository(
                    id = 2,
                    name = "very-long-repository-name-that-might-break-the-ui-layout-if-not-handled",
                    fullName = "organization/very-long-repository-name-that-might-break-the-ui-layout-if-not-handled",
                    description = "Short description",
                    starsCount = 99,
                    language = "TypeScript",
                    ownerName = "organization",
                    ownerAvatarUrl = ""
                ),
                onClick = {}
            )
        }
    }
}