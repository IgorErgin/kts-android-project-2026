package com.github.igorergin.ktsandroid.feature.profile.domain.model

/**
 * Основная модель данных профиля пользователя GitHub.
 */
data class UserProfile(
    val id: String,
    val login: String,
    val name: String,
    val avatarUrl: String,
    val location: String,
    val bio: String
)