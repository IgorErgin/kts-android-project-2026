package com.github.igorergin.ktsandroid.feature.detail.data.repository

import com.github.igorergin.ktsandroid.core.network.NetworkClient
import com.github.igorergin.ktsandroid.feature.detail.domain.model.RepositoryDetail
import com.github.igorergin.ktsandroid.feature.repositories.data.network.GithubRepositoryDto
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlin.coroutines.cancellation.CancellationException

/**
 * Репозиторий для получения детальной информации о репозитории.
 */
class DetailRepository {
    private val client = NetworkClient.httpClient

    suspend fun getRepositoryDetails(owner: String, repo: String): Result<RepositoryDetail> {
        return try {
            // Запрос к API: GET /repos/{owner}/{repo}
            val response: GithubRepositoryDto = client.get("repos/$owner/$repo").body()

            val domainModel = RepositoryDetail(
                id = response.id,
                name = response.name,
                fullName = response.fullName,
                description = response.description ?: "Нет описания",
                starsCount = response.stargazersCount,
                forksCount = 0,
                openIssuesCount = 0,
                ownerLogin = response.owner.login,
                ownerAvatarUrl = response.owner.avatarUrl,
                htmlUrl = "",
                language = response.language
            )
            Result.success(domainModel)

        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}