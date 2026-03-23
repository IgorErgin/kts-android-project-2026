package com.github.igorergin.ktsandroid.feature.onboarding.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.igorergin.ktsandroid.core.designsystem.common.AppButton
import com.github.igorergin.ktsandroid.core.designsystem.theme.AppTheme
import com.github.igorergin.ktsandroid.core.designsystem.theme.GitHubTextSecondary
import com.github.igorergin.ktsandroid.feature.onboarding.domain.model.OnboardingPage
import ktsandroidproject.composeapp.generated.resources.Res
import ktsandroidproject.composeapp.generated.resources.ic_onboarding_1
import ktsandroidproject.composeapp.generated.resources.ic_onboarding_2
import ktsandroidproject.composeapp.generated.resources.ic_onboarding_3
import ktsandroidproject.composeapp.generated.resources.onboarding_1_desc
import ktsandroidproject.composeapp.generated.resources.onboarding_1_title
import ktsandroidproject.composeapp.generated.resources.onboarding_2_desc
import ktsandroidproject.composeapp.generated.resources.onboarding_2_title
import ktsandroidproject.composeapp.generated.resources.onboarding_3_desc
import ktsandroidproject.composeapp.generated.resources.onboarding_3_title
import ktsandroidproject.composeapp.generated.resources.welcome_next
import ktsandroidproject.composeapp.generated.resources.welcome_skip
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WelcomeScreen(onNavigateToLogin: () -> Unit) {
    val pages = remember {
        listOf(
            OnboardingPage(
                title = Res.string.onboarding_1_title,
                description = Res.string.onboarding_1_desc,
                imageRes = Res.drawable.ic_onboarding_1
            ),
            OnboardingPage(
                title = Res.string.onboarding_2_title,
                description = Res.string.onboarding_2_desc,
                imageRes = Res.drawable.ic_onboarding_2
            ),
            OnboardingPage(
                title = Res.string.onboarding_3_title,
                description = Res.string.onboarding_3_desc,
                imageRes = Res.drawable.ic_onboarding_3
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
                text = if (pagerState.currentPage == pages.size - 1) stringResource(Res.string.welcome_next) else stringResource(
                    Res.string.welcome_skip
                ),
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
        Image(
            painter = painterResource(page.imageRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
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

@Preview
@Composable
private fun OnboardingContentPreviewLight() {
    AppTheme(darkTheme = false) {
        Surface(modifier = Modifier.fillMaxSize()) {
            OnboardingContent(
                page = OnboardingPage(
                    title = Res.string.onboarding_1_title,
                    description = Res.string.onboarding_1_desc,
                    imageRes = Res.drawable.ic_onboarding_1
                )
            )
        }
    }
}

@Preview
@Composable
private fun OnboardingContentPreviewDark() {
    AppTheme(darkTheme = true) {
        Surface(modifier = Modifier.fillMaxSize()) {
            OnboardingContent(
                page = OnboardingPage(
                    title = Res.string.onboarding_1_title,
                    description = Res.string.onboarding_1_desc,
                    imageRes = Res.drawable.ic_onboarding_1
                )
            )
        }
    }
}