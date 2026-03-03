package com.github.igorergin.ktsandroid.domain.model

import org.jetbrains.compose.resources.StringResource

/**
 * Модель данных для страницы онбординга.
 */
data class OnboardingPage(
    val title: StringResource,
    val description: StringResource,
    val imageUrl: String
)