package com.github.igorergin.ktsandroid.feature.detail.presentation

import com.github.igorergin.ktsandroid.core.domain.error.AppError
import org.jetbrains.compose.resources.StringResource

sealed interface DetailSideEffect {
    data class ShowError(val error: AppError) : DetailSideEffect
    data class ShowMessage(val message: StringResource) : DetailSideEffect
}
