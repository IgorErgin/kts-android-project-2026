package com.github.igorergin.ktsandroid.feature.onboarding.domain.model

import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

/**
 * Модель данных для страницы онбординга.
 */
data class OnboardingPage(
    val title: StringResource,
    val description: StringResource,
    val imageRes: DrawableResource
)