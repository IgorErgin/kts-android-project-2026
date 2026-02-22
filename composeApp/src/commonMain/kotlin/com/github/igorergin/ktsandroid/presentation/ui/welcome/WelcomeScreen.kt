package com.github.igorergin.ktsandroid.presentation.ui.welcome

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import kotlinx.coroutines.delay
import ktsandroidproject.composeapp.generated.resources.Res
import ktsandroidproject.composeapp.generated.resources.compose_multiplatform
import org.jetbrains.compose.resources.painterResource

@Composable
fun WelcomeScreen(onNavigateToLogin: () -> Unit) {
    // Состояние для плавного появления текста и кнопки
    var isContentVisible by rememberSaveable { mutableStateOf(false) }

    // Запускаем таймер при открытии экрана
    LaunchedEffect(Unit) {
        delay(300)
        isContentVisible = true
    }

    // Анимация "левитации" для картинки
    val infiniteTransition = rememberInfiniteTransition(label = "floating")
    val floatingOffsetY by infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offsetY"
    )

    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFF1E1E2E), Color(0xFF2D2D44))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // --- ПАРЯЩАЯ КАРТИНКА С COIL ---
            Box(
                modifier = Modifier
                    .offset(y = floatingOffsetY.dp) // Применяем анимацию сдвига
                    .size(220.dp)
                    .shadow(16.dp, CircleShape)
                    .clip(CircleShape)
                    .border(4.dp, Color.White.copy(alpha = 0.8f), CircleShape)
            ) {
                AsyncImage(
                    model = "https://avatars.githubusercontent.com/u/32689599?s=280&v=4",
                    contentDescription = "Welcome Graphic",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    placeholder = painterResource(Res.drawable.compose_multiplatform),
                    error = painterResource(Res.drawable.compose_multiplatform)
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // --- ПЛАВНО ПОЯВЛЯЮЩИЙСЯ ТЕКСТ И КНОПКА ---
            AnimatedVisibility(
                visible = isContentVisible,
                enter = fadeIn(animationSpec = tween(800)) + slideInVertically(
                    initialOffsetY = { 50 },
                    animationSpec = tween(800)
                )
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Добро пожаловать",
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Войди, чтобы продолжить",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.7f)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = onNavigateToLogin,
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF6200EA)
                        )
                    ) {
                        Text("Поехали!", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun WelcomeScreenPreview() {
    MaterialTheme {
        WelcomeScreen(onNavigateToLogin = {})
    }
}

