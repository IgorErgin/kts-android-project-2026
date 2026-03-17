package com.github.igorergin.ktsandroid.feature.auth.presentation

import androidx.compose.runtime.staticCompositionLocalOf
import com.github.igorergin.ktsandroid.feature.auth.domain.AuthManager

val LocalAuthManager = staticCompositionLocalOf<AuthManager> {
    error("AuthManager not provided! Make sure it's provided at the platform level in MainActivity.")
}