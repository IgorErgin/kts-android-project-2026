package com.github.igorergin.ktsandroid.feature.detail.data.repository

import com.github.igorergin.ktsandroid.core.network.safeApiCall
import com.github.igorergin.ktsandroid.feature.detail.data.remote.CreateIssueRequest
import com.github.igorergin.ktsandroid.feature.detail.data.remote.GithubContentDto
import com.github.igorergin.ktsandroid.feature.detail.data.remote.IssueResponse
import com.github.igorergin.ktsandroid.feature.detail.data.remote.PullRequestDto
import com.github.igorergin.ktsandroid.feature.detail.data.remote.ReadmeResponse
import com.github.igorergin.ktsandroid.feature.detail.data.remote.UploadFileRequest
import com.github.igorergin.ktsandroid.feature.detail.data.remote.UploadFileResponse
import com.github.igorergin.ktsandroid.feature.detail.domain.model.GithubContent
import com.github.igorergin.ktsandroid.feature.detail.domain.model.PullRequest
import com.github.igorergin.ktsandroid.feature.detail.domain.model.RepositoryDetail
import com.github.igorergin.ktsandroid.feature.repositories.data.remote.model.GithubRepoDto
import com.github.igorergin.ktsandroid.feature.repositories.domain.model.RepositoryId
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
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
            id = RepositoryId(response.id),
            name = response.name,
            fullName = response.fullName,
            description = response.description ?: "",
            starsCount = response.starsCount,
            forksCount = response.forksCount ?: 0,
            openIssuesCount = response.openIssuesCount ?: 0,
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

    suspend fun getContent(owner: String, repo: String, path: String = ""): Result<List<GithubContent>> = safeApiCall {
        val response: List<GithubContentDto> = httpClient.get {
            url {
                appendPathSegments("repos", owner, repo, "contents")
                if (path.isNotEmpty()) {
                    appendPathSegments(path)
                }
            }
        }.body()

        response.map { dto ->
            if (dto.type == "dir") {
                GithubContent.Directory(dto.name, dto.path, dto.sha)
            } else {
                GithubContent.File(
                    name = dto.name,
                    path = dto.path,
                    sha = dto.sha,
                    size = dto.size,
                    downloadUrl = dto.downloadUrl,
                    content = dto.content,
                    encoding = dto.encoding
                )
            }
        }
    }

    suspend fun uploadFile(
        owner: String,
        repo: String,
        path: String,
        message: String,
        contentBase64: String,
        sha: String? = null
    ): Result<UploadFileResponse> = safeApiCall {
        httpClient.put {
            url { appendPathSegments("repos", owner, repo, "contents", path) }
            contentType(ContentType.Application.Json)
            setBody(UploadFileRequest(message, contentBase64, sha))
        }.body()
    }

    suspend fun createIssue(owner: String, repo: String, title: String, body: String): Result<IssueResponse> = safeApiCall {
        httpClient.post {
            url { appendPathSegments("repos", owner, repo, "issues") }
            contentType(ContentType.Application.Json)
            setBody(CreateIssueRequest(title, body))
        }.body()
    }

    suspend fun getPullRequests(owner: String, repo: String): Result<List<PullRequest>> = safeApiCall {
        val response: List<PullRequestDto> = httpClient.get {
            url { appendPathSegments("repos", owner, repo, "pulls") }
        }.body()

        response.map { dto ->
            PullRequest(
                id = dto.id,
                number = dto.number,
                state = dto.state,
                title = dto.title,
                body = dto.body ?: "",
                htmlUrl = dto.htmlUrl,
                createdAt = dto.createdAt,
                updatedAt = dto.updatedAt,
                userLogin = dto.user.login,
                userAvatarUrl = dto.user.avatarUrl
            )
        }
    }
}