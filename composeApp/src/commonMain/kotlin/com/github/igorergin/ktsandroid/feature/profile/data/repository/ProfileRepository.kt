package com.github.igorergin.ktsandroid.feature.profile.data.repository

import com.github.igorergin.ktsandroid.feature.profile.domain.model.UserProfile
import kotlinx.coroutines.delay

/**
 * Репозиторий для получения данных профиля.
 * Использует моковые данные под GitHub.
 */
class ProfileRepository {
    suspend fun getProfile(): Result<UserProfile> {
        delay(1000) // Имитация задержки сети

        val mockProfile = UserProfile(
            id = "1",
            login = "octocat",
            name = "The Octocat",
            avatarUrl = "https://avatars.githubusercontent.com/u/583231?v=4",
            location = "San Francisco",
            bio = "I am the official GitHub mascot!"
        )

        return Result.success(mockProfile)
    }
}