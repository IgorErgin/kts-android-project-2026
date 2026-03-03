package com.github.igorergin.ktsandroid.presentation.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.github.igorergin.ktsandroid.domain.model.Post
import com.github.igorergin.ktsandroid.domain.model.UserProfile
import com.github.igorergin.ktsandroid.presentation.common.VKButton
import com.github.igorergin.ktsandroid.presentation.ui.main.components.PostCard
import com.github.igorergin.ktsandroid.theme.AppTheme
import com.github.igorergin.ktsandroid.theme.VKSecondaryText

@Composable
fun MainScreen(
    mainViewModel: MainViewModel = viewModel { MainViewModel() }
) {
    val state by mainViewModel.state.collectAsState()

    val onEditAvatarClick = remember { { /* Будущая логика */ } }

    MainScreenContent(
        state = state,
        posts = mainViewModel.dummyPosts,
        onEditAvatarClick = onEditAvatarClick
    )
}
@Composable
fun MainScreenContent(
    state: MainUiState,
    posts: List<Post>,
    onEditAvatarClick: () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
                state.error != null -> {
                    Text(
                        text = state.error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                state.profile != null -> {
                    ProfileContent(
                        profile = state.profile,
                        posts = posts,
                        onEditAvatarClick = onEditAvatarClick
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileContent(
    profile: UserProfile,
    posts: List<Post>,
    onEditAvatarClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item(key = "profile_header") {
            ProfileHeader(
                profile = profile,
                onEditAvatarClick = onEditAvatarClick
            )
        }

        item(key = "posts_title") {
            Text(
                text = "Мои записи",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )
        }

        items(
            items = posts,
            key = { post -> "post_${post.id}" }
        ) { post ->
            PostCard(post = post)
        }
    }
}

@Composable
private fun ProfileHeader(
    profile: UserProfile,
    onEditAvatarClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = profile.avatarUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "${profile.firstName} ${profile.lastName}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Город: ${profile.city} • Родился: ${profile.dateOfBirth}",
                style = MaterialTheme.typography.bodyMedium,
                color = VKSecondaryText
            )

            Spacer(modifier = Modifier.height(24.dp))

            VKButton(
                text = "Изменить аватар",
                onClick = onEditAvatarClick
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    val mockProfile = UserProfile("1", "Иван", "Иванов", "", "Москва", "01.01.1990")
    AppTheme {
        MainScreenContent(
            state = MainUiState(profile = mockProfile, isLoading = false),
            posts = listOf(Post(1, "Тестовый пост", "Сегодня")),
            onEditAvatarClick = {}
        )
    }
}