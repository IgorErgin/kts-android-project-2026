package com.github.igorergin.ktsandroid.feature.auth.presentation

/**
 * Одноразовые события экрана логина
 */
sealed class LoginUiEvent {
    object LoginSuccessEvent : LoginUiEvent()
}