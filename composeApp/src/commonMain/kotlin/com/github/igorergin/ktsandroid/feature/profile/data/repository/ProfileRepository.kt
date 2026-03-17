package com.github.igorergin.ktsandroid.feature.profile.data.repository

import com.github.igorergin.ktsandroid.feature.profile.data.dto.UserProfileDto
import com.github.igorergin.ktsandroid.feature.profile.domain.model.UserProfile
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class ProfileRepository(private val httpClient: HttpClient) {

    suspend fun getCurrentUser(): UserProfile {
        val dto: UserProfileDto = httpClient.get("https://api.github.com/user").body()

        return UserProfile(
            id = dto.id,
            login = dto.login,
            name = dto.name,
            avatarUrl = dto.avatarUrl,
            bio = dto.bio,
        )
    }
}