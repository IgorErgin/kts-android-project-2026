package com.github.igorergin.ktsandroid.feature.detail.data.repository

import com.github.igorergin.ktsandroid.core.network.safeApiCall
import com.github.igorergin.ktsandroid.feature.detail.data.remote.CreateIssueRequest
import com.github.igorergin.ktsandroid.feature.detail.data.remote.IssueResponse
import com.github.igorergin.ktsandroid.feature.detail.data.remote.ReadmeResponse
import com.github.igorergin.ktsandroid.feature.detail.domain.model.RepositoryDetail
import com.github.igorergin.ktsandroid.feature.repositories.data.remote.model.GithubRepoDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.appendPathSegments
import io.ktor.http.contentType
import io.ktor.util.decodeBase64String

class DetailRepository(
    private val httpClient: HttpClient
) {
    suspend fun getRepositoryDetails(owner: String, repo: String): Result<RepositoryDetail> = safeApiCall {
        val response: GithubRepoDto = httpClient.get {
            url { appendPathSegments("repos", owner, repo) }
        }.body()

        RepositoryDetail(
            id = response.id,
            name = response.name,
            fullName = response.fullName,
            description = response.description ?: "",
            starsCount = response.starsCount,
            forksCount = 0,
            openIssuesCount = 0,
            ownerLogin = response.owner.login,
            ownerAvatarUrl = response.owner.avatarUrl,
            htmlUrl = "https://github.com/$owner/$repo",
            language = response.language
        )
    }

    suspend fun getReadme(owner: String, repo: String): Result<String> = safeApiCall {
        val response: ReadmeResponse = httpClient.get {
            url { appendPathSegments("repos", owner, repo, "readme") }
        }.body()

        if (response.encoding == "base64") {
            response.content.replace("\n", "").decodeBase64String()
        } else {
            response.content
        }
    }

    suspend fun createIssue(owner: String, repo: String, title: String, body: String): Result<IssueResponse> = safeApiCall {
        httpClient.post {
            url { appendPathSegments("repos", owner, repo, "issues") }
            contentType(ContentType.Application.Json)
            setBody(CreateIssueRequest(title, body))
        }.body()
    }
}