package com.github.igorergin.ktsandroid.feature.onboarding.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.github.igorergin.ktsandroid.core.designsystem.components.AppButton
import com.github.igorergin.ktsandroid.feature.onboarding.domain.model.OnboardingPage
import com.github.igorergin.ktsandroid.core.designsystem.theme.AppTheme
import com.github.igorergin.ktsandroid.core.designsystem.theme.GitHubTextSecondary
import ktsandroidproject.composeapp.generated.resources.Res
import ktsandroidproject.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WelcomeScreen(onNavigateToLogin: () -> Unit) {
    // Данные для карусели
    val pages = remember {
        listOf(
            OnboardingPage(
                title = Res.string.onboarding_1_title,
                description = Res.string.onboarding_1_desc,
                imageUrl = "https://github.githubassets.com/images/modules/logos_page/GitHub-Mark.png"
            ),
            OnboardingPage(
                title = Res.string.onboarding_2_title,
                description = Res.string.onboarding_2_desc,
                imageUrl = "https://github.githubassets.com/images/modules/logos_page/GitHub-Mark.png"
            ),
            OnboardingPage(
                title = Res.string.onboarding_3_title,
                description = Res.string.onboarding_3_desc,
                imageUrl = "https://github.githubassets.com/images/modules/logos_page/GitHub-Mark.png"
            )
        )
    }

    val pagerState = rememberPagerState(pageCount = { pages.size })

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Карусель
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { index ->
                OnboardingContent(pages[index])
            }
            Row(
                Modifier
                    .height(50.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(pages.size) { iteration ->
                    val color = if (pagerState.currentPage == iteration)
                        MaterialTheme.colorScheme.primary
                    else
                        GitHubTextSecondary.copy(alpha = 0.5f)

                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .clip(CircleShape)
                            .background(color)
                            .size(8.dp)
                    )
                }
            }

            AppButton(
                text = if (pagerState.currentPage == pages.size - 1) stringResource(Res.string.welcome_next) else stringResource(Res.string.welcome_skip),
                onClick = onNavigateToLogin,
                modifier = Modifier
                    .padding(24.dp)
            )
        }
    }
}

@Preview
@Composable
fun WelcomeScreenPreview() {
    AppTheme {
        WelcomeScreen(onNavigateToLogin = {})
    }
}

@Composable
fun OnboardingContent(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AsyncImage(
            model = page.imageUrl,
            contentDescription = null,
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
                .background(Color.White)
        )

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = stringResource(page.title),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(page.description),
            style = MaterialTheme.typography.bodyLarge,
            color = GitHubTextSecondary,
            textAlign = TextAlign.Center
        )
    }
}