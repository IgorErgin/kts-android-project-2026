package com.github.igorergin.ktsandroid.data

import com.github.igorergin.ktsandroid.domain.model.UserProfile
import kotlinx.coroutines.delay

/**
 * Репозиторий для получения данных профиля.
 * Пока использует моковые (тестовые) данные.
 */
class ProfileRepository {
    suspend fun getProfile(): Result<UserProfile> {
        delay(1000) // Имитация задержки сети

        val mockProfile = UserProfile(
            id = "1",
            firstName = "Иван",
            lastName = "Иванов",
            avatarUrl = "https://avatars.githubusercontent.com/u/32689599?s=280&v=4",
            city = "Москва",
            dateOfBirth = "01.01.1990"
        )

        return Result.success(mockProfile)
    }
}