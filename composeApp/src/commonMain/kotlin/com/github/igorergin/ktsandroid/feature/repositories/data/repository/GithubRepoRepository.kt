package com.github.igorergin.ktsandroid.feature.repositories.data.repository

import com.github.igorergin.ktsandroid.core.network.NetworkClient
import com.github.igorergin.ktsandroid.feature.repositories.data.network.SearchResponse
import com.github.igorergin.ktsandroid.feature.repositories.domain.model.GithubRepository
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class GithubRepoRepository {
    private val client = NetworkClient.httpClient

    suspend fun searchRepositories(query: String, page: Int, perPage: Int = 20): Result<List<GithubRepository>> {
        return withContext(Dispatchers.IO) {
            try {
                val response: SearchResponse = client.get("https://api.github.com/search/repositories") {
                    parameter("q", query)
                    parameter("page", page)
                    parameter("per_page", perPage)
                }.body()

                val domainModels = response.items.map { dto ->
                    GithubRepository(
                        id = dto.id,
                        name = dto.name,
                        fullName = dto.fullName,
                        description = dto.description ?: "Нет описания",
                        language = dto.language ?: "Unknown",
                        starsCount = dto.stargazersCount,
                        ownerName = dto.owner.login,
                        ownerAvatarUrl = dto.owner.avatarUrl
                    )
                }
                Result.success(domainModels)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}