package com.github.igorergin.ktsandroid.feature.profile.domain.model

/**
 * Основная модель данных профиля пользователя GitHub.
 */
data class UserProfile(
    val id: String,
    val login: String,
    val name: String?,
    val avatarUrl: String,
    val bio: String?,
    val followersCount: Int,
    val publicReposCount: Int
)