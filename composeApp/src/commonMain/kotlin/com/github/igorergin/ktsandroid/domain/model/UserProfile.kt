package com.github.igorergin.ktsandroid.domain.model

/**
 * Основная модель данных профиля пользователя.
 */
data class UserProfile(
    val id: String,
    val firstName: String,
    val lastName: String,
    val avatarUrl: String,
    val city: String,
    val dateOfBirth: String
)