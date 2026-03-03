package com.github.igorergin.ktsandroid.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Основные цвета VK
val VKBlue = Color(0xFF0077FF)
val VKLightGray = Color(0xFFEBEDF0)
val VKDarkGray = Color(0xFF19191A)
val VKSecondaryText = Color(0xFF818C99)

private val DarkColorScheme = darkColorScheme(
    primary = VKBlue,
    background = VKDarkGray,
    surface = Color(0xFF222223),
    onPrimary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    secondary = VKSecondaryText
)

private val LightColorScheme = lightColorScheme(
    primary = VKBlue,
    background = VKLightGray,
    surface = Color.White,
    onPrimary = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black,
    secondary = VKSecondaryText
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