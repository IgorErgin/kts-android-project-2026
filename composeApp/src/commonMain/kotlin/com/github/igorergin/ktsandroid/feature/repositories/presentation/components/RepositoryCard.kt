package com.github.igorergin.ktsandroid.feature.repositories.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.github.igorergin.ktsandroid.core.designsystem.theme.GitHubTextSecondary
import com.github.igorergin.ktsandroid.feature.repositories.domain.model.GithubRepository

@Composable
fun RepositoryCard(repo: GithubRepository, onClick: () -> Unit) {
    Card(shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth().clickable { onClick() }) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(model = repo.ownerAvatarUrl, contentDescription = null, modifier = Modifier.size(24.dp).clip(CircleShape).background(Color.Gray))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = repo.fullName, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                Text(text = "⭐ ${repo.starsCount}", style = MaterialTheme.typography.labelMedium, color = GitHubTextSecondary)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = repo.description, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Language: ${repo.language}", style = MaterialTheme.typography.labelSmall, color = GitHubTextSecondary)
        }
    }
}