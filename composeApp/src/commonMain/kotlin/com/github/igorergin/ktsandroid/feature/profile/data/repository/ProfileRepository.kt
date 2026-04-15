package com.github.igorergin.ktsandroid.feature.profile.data.repository

import com.github.igorergin.ktsandroid.core.network.safeApiCall
import com.github.igorergin.ktsandroid.feature.profile.data.dto.UserProfileDto
import com.github.igorergin.ktsandroid.feature.profile.domain.model.UserProfile
import com.github.igorergin.ktsandroid.feature.repositories.data.mapper.toDomain
import com.github.igorergin.ktsandroid.feature.repositories.domain.model.GithubRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class ProfileRepository(private val httpClient: HttpClient) {

    suspend fun getCurrentUser(): Result<UserProfile> = safeApiCall {
        val dto: UserProfileDto = httpClient.get("https://api.github.com/user").body()

        UserProfile(
            id = dto.id,
            login = dto.login,
            name = dto.name,
            avatarUrl = dto.avatarUrl,
            bio = dto.bio,
            followersCount = dto.followersCount ?: 0,
            publicReposCount = dto.publicReposCount ?: 0
        )
    }

    suspend fun getUserRepos(): Result<List<GithubRepository>> = safeApiCall {
        val dtos: List<com.github.igorergin.ktsandroid.feature.repositories.data.remote.model.GithubRepoDto> = 
            httpClient.get("https://api.github.com/user/repos").body()
        dtos.map { it.toDomain() }
    }

    suspend fun getUserActivity(username: String): Result<List<com.github.igorergin.ktsandroid.feature.profile.domain.model.GithubEvent>> = safeApiCall {
        val dtos: List<com.github.igorergin.ktsandroid.feature.profile.data.dto.GithubEventDto> = 
            httpClient.get("https://api.github.com/users/$username/events").body()
        
        dtos.map { dto ->
            com.github.igorergin.ktsandroid.feature.profile.domain.model.GithubEvent(
                id = dto.id,
                type = dto.type,
                actorLogin = dto.actor.login,
                actorAvatarUrl = dto.actor.avatarUrl,
                repoName = dto.repo.name,
                createdAt = dto.createdAt,
                payloadAction = dto.payload?.action,
                commitMessages = dto.payload?.commits?.map { it.message } ?: emptyList()
            )
        }
    }
}
