package com.github.igorergin.ktsandroid.feature.repositories.data.remote

import com.github.igorergin.ktsandroid.feature.repositories.data.remote.model.GithubSearchResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.put
import io.ktor.http.HttpHeaders
import io.ktor.http.path

interface GithubRemoteDataSource {
    suspend fun searchRepositories(query: String, page: Int, perPage: Int): GithubSearchResponse
    suspend fun starRepository(owner: String, name: String): Result<Unit>
    suspend fun unstarRepository(owner: String, name: String): Result<Unit>
}

class GithubRemoteDataSourceImpl(
    private val httpClient: HttpClient
) : GithubRemoteDataSource {

    override suspend fun searchRepositories(query: String, page: Int, perPage: Int): GithubSearchResponse {
        return httpClient.get {
            url {
                protocol = io.ktor.http.URLProtocol.HTTPS
                host = "api.github.com"
                path("search", "repositories")
                parameter("q", query)
                parameter("page", page)
                parameter("per_page", perPage)
            }
        }.body()
    }

    override suspend fun starRepository(owner: String, name: String): Result<Unit> = runCatching {
        httpClient.put {
            url {
                protocol = io.ktor.http.URLProtocol.HTTPS
                host = "api.github.com"
                path("user", "starred", owner, name)
            }
            header(HttpHeaders.ContentLength, 0)
        }
    }

    override suspend fun unstarRepository(owner: String, name: String): Result<Unit> = runCatching {
        httpClient.delete {
            url {
                protocol = io.ktor.http.URLProtocol.HTTPS
                host = "api.github.com"
                path("user", "starred", owner, name)
            }
        }
    }
}
