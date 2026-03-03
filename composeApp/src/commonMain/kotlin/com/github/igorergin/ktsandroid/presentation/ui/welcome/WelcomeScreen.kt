package com.github.igorergin.ktsandroid.presentation.ui.welcome

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.igorergin.ktsandroid.theme.AppTheme
import com.github.igorergin.ktsandroid.theme.VKSecondaryText
import androidx.compose.ui.tooling.preview.Preview
import coil3.compose.AsyncImage
import com.github.igorergin.ktsandroid.presentation.common.VKButton
import com.github.igorergin.ktsandroid.domain.model.OnboardingPage
import ktsandroidproject.composeapp.generated.resources.Res
import ktsandroidproject.composeapp.generated.resources.onboarding_1_desc
import ktsandroidproject.composeapp.generated.resources.onboarding_1_title
import ktsandroidproject.composeapp.generated.resources.onboarding_2_desc
import ktsandroidproject.composeapp.generated.resources.onboarding_2_title
import ktsandroidproject.composeapp.generated.resources.onboarding_3_desc
import ktsandroidproject.composeapp.generated.resources.onboarding_3_title
import ktsandroidproject.composeapp.generated.resources.welcome_next
import ktsandroidproject.composeapp.generated.resources.welcome_skip
import org.jetbrains.compose.resources.getString
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
                imageUrl = "https://avatars.githubusercontent.com/u/32689599?s=280&v=4"
            ),
            OnboardingPage(
                Res.string.onboarding_2_title,
                Res.string.onboarding_2_desc,
                "https://avatars.githubusercontent.com/u/32689599?s=280&v=4"
            ),
            OnboardingPage(
                Res.string.onboarding_3_title,
                Res.string.onboarding_3_desc,
                "https://avatars.githubusercontent.com/u/32689599?s=280&v=4"
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
                        VKSecondaryText.copy(alpha = 0.5f)

                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .clip(CircleShape)
                            .background(color)
                            .size(8.dp)
                    )
                }
            }

            VKButton(
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
        )

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = stringResource(page.title),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Companion.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(page.description),
            style = MaterialTheme.typography.bodyLarge,
            color = VKSecondaryText,
            textAlign = TextAlign.Center
        )
    }
}