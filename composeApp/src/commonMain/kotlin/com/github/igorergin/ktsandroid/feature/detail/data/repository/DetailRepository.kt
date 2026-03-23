package com.github.igorergin.ktsandroid.feature.detail.data.repository

import com.github.igorergin.ktsandroid.feature.detail.domain.model.RepositoryDetail
import com.github.igorergin.ktsandroid.feature.repositories.data.remote.model.GithubRepoDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.CancellationException

/**
 * Репозиторий для получения детальной информации о репозитории.
 */
class DetailRepository(
    private val httpClient: HttpClient
) {
    suspend fun getRepositoryDetails(owner: String, repo: String): Result<RepositoryDetail> {
        return try {
            // Запрос к API: GET /repos/{owner}/{repo}
            val response: GithubRepoDto = httpClient.get("https://api.github.com/repos/$owner/$repo").body()

            val domainModel = RepositoryDetail(
                id = response.id,
                name = response.name,
                fullName = response.fullName,
                description = response.description ?: "Нет описания",
                starsCount = response.starsCount,
                forksCount = 0,
                openIssuesCount = 0,
                ownerLogin = response.owner.login,
                ownerAvatarUrl = response.owner.avatarUrl,
                htmlUrl = "",
                language = response.language ?: "Unknown"
            )
            Result.success(domainModel)

        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}