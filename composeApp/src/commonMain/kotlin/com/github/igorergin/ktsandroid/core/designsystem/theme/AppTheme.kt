package com.github.igorergin.ktsandroid.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Основные цвета GitHub
val GitHubDarkBg = Color(0xFF0D1117)
val GitHubDarkSurface = Color(0xFF161B22)
val GitHubLightBg = Color(0xFFF6F8FA)
val GitHubLightSurface = Color(0xFFFFFFFF)
val GitHubBlue = Color(0xFF2F81F7)
val GitHubTextSecondary = Color(0xFF8B949E)

private val DarkColorScheme = darkColorScheme(
    primary = GitHubBlue,
    background = GitHubDarkBg,
    surface = GitHubDarkSurface,
    onPrimary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    secondary = GitHubTextSecondary
)

private val LightColorScheme = lightColorScheme(
    primary = GitHubBlue,
    background = GitHubLightBg,
    surface = GitHubLightSurface,
    onPrimary = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black,
    secondary = GitHubTextSecondary
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}