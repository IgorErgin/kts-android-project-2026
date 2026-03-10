package com.github.igorergin.ktsandroid.feature.detail.data.repository

import com.github.igorergin.ktsandroid.core.network.NetworkClient
import com.github.igorergin.ktsandroid.feature.repositories.data.network.GithubRepositoryDto
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

/**
 * Репозиторий для получения детальной информации о репозитории.
 */
class DetailRepository {
    private val client = NetworkClient.httpClient

    suspend fun getRepositoryDetails(owner: String, repo: String): Result<GithubRepositoryDto> {
        return withContext(Dispatchers.IO) {
            try {
                // Запрос к API: GET /repos/{owner}/{repo}
                val response: GithubRepositoryDto = client.get("repos/$owner/$repo").body()
                Result.success(response)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}