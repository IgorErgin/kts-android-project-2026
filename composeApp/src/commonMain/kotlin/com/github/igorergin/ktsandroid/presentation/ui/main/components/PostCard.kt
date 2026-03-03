package com.github.igorergin.ktsandroid.presentation.ui.main.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.igorergin.ktsandroid.domain.model.Post
import com.github.igorergin.ktsandroid.theme.VKSecondaryText

/**
 * Компонент карточки поста.
 */
@Composable
fun PostCard(post: Post) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = post.date,
                style = MaterialTheme.typography.labelMedium,
                color = VKSecondaryText
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = post.text,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}