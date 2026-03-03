package com.github.igorergin.ktsandroid.presentation.ui.login

/**
 * Одноразовые события экрана логина
 */
sealed class LoginUiEvent {
    object LoginSuccessEvent : LoginUiEvent()
}