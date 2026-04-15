package com.github.igorergin.ktsandroid.feature.repositories.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import coil3.compose.AsyncImage
import com.github.igorergin.ktsandroid.core.designsystem.theme.AppTheme
import com.github.igorergin.ktsandroid.feature.repositories.domain.model.GithubRepository

@Composable
fun RepositoryCard(
    repo: GithubRepository,
    isFavorite: Boolean = false,
    onFavoriteClick: () -> Unit = {},
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

                IconButton(onClick = onFavoriteClick) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Star else Icons.Outlined.StarBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color(0xFFFFD700) else MaterialTheme.colorScheme.outline
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

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
                Text(
                    text = "⭐ ${repo.starsCount}",
                    style = MaterialTheme.typography.labelMedium
                )
                Spacer(modifier = Modifier.width(16.dp))
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondary)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = repo.language.ifBlank { "Unknown" },
                    style = MaterialTheme.typography.labelSmall
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
                    name = "kts-android",
                    fullName = "igorergin/kts-android",
                    description = "Учебный проект по разработке на Kotlin Multiplatform",
                    starsCount = 42,
                    language = "Kotlin",
                    ownerName = "igorergin",
                    ownerAvatarUrl = ""
                ),
                isFavorite = false,
                onClick = {}
            )
        }
    }
}

@Preview
@Composable
private fun RepositoryCardFavoritePreview() {
    AppTheme(darkTheme = true) {
        Surface(modifier = Modifier.padding(16.dp)) {
            RepositoryCard(
                repo = GithubRepository(
                    id = 1,
                    name = "kts-android",
                    fullName = "igorergin/kts-android",
                    description = "Избранный репозиторий в темной теме",
                    starsCount = 999,
                    language = "Kotlin",
                    ownerName = "igorergin",
                    ownerAvatarUrl = ""
                ),
                isFavorite = true,
                onClick = {}
            )
        }
    }
}