package com.github.igorergin.ktsandroid.feature.repositories.data.repository

import com.github.igorergin.ktsandroid.feature.repositories.data.local.RepositoryDao
import com.github.igorergin.ktsandroid.feature.repositories.data.mapper.toDomain
import com.github.igorergin.ktsandroid.feature.repositories.data.mapper.toEntity
import com.github.igorergin.ktsandroid.feature.repositories.data.remote.model.GithubSearchResponse
import com.github.igorergin.ktsandroid.feature.repositories.domain.model.GithubRepository
import com.github.igorergin.ktsandroid.feature.repositories.domain.repository.GithubRepoRepository
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GithubRepoRepositoryImpl(
    private val httpClient: HttpClient,
    private val repositoryDao: RepositoryDao
) : GithubRepoRepository {

    private val TAG = "GithubRepoRepo"

    override fun searchRepositories(
        query: String,
        page: Int,
        perPage: Int,
        forceRefresh: Boolean
    ): Flow<List<GithubRepository>> = flow {

        Napier.i("Starting search: query=$query, page=$page", tag = TAG)

        if (page == 1) {
            val cachedRepos = repositoryDao.getAllRepositories().map { it.toDomain() }
            if (cachedRepos.isNotEmpty()) {
                Napier.d("Emitting ${cachedRepos.size} items from local cache", tag = TAG)
                emit(cachedRepos)
            }
        }

        try {
            Napier.d("Fetching page $page from network...", tag = TAG)
            val response: GithubSearchResponse = httpClient.get("https://api.github.com/search/repositories") {
                parameter("q", query)
                parameter("page", page)
                parameter("per_page", perPage)
            }.body()

            val networkEntities = response.items.map { it.toEntity() }

            if (forceRefresh || page == 1) {
                Napier.i("Clearing old cache for a fresh start", tag = TAG)
                repositoryDao.clearAll()
            }

            repositoryDao.insertRepositories(networkEntities)

            val finalData = repositoryDao.getAllRepositories().map { it.toDomain() }
            emit(finalData)

        } catch (e: Exception) {
            Napier.e("Error during searchRepositories", e, tag = TAG)
            if (page == 1) {
                val cachedRepos = repositoryDao.getAllRepositories()
                if (cachedRepos.isEmpty()) {
                    Napier.w("No cache available after network error. Propagating exception.", tag = TAG)
                    throw e
                } else {
                    Napier.w("Network failed, but user sees cached data.", tag = TAG)
                }
            } else {
                throw e
            }
        }
    }

    override suspend fun clearLocalData() {
        Napier.i("Clearing all local repository data (Logout/Reset)", tag = TAG)
        repositoryDao.clearAll()
    }
}