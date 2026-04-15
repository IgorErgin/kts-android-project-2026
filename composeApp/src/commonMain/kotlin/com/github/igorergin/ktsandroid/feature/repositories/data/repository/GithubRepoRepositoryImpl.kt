package com.github.igorergin.ktsandroid.feature.repositories.data.repository

import com.github.igorergin.ktsandroid.core.domain.error.AppError
import com.github.igorergin.ktsandroid.core.domain.error.AppErrorException
import com.github.igorergin.ktsandroid.feature.repositories.data.local.GithubLocalDataSource
import com.github.igorergin.ktsandroid.feature.repositories.data.mapper.toDomain
import com.github.igorergin.ktsandroid.feature.repositories.data.remote.GithubRemoteDataSource
import com.github.igorergin.ktsandroid.feature.repositories.domain.model.GithubRepository
import com.github.igorergin.ktsandroid.feature.repositories.domain.repository.GithubRepoRepository
import io.github.aakira.napier.Napier
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GithubRepoRepositoryImpl(
    private val remoteDataSource: GithubRemoteDataSource,
    private val localDataSource: GithubLocalDataSource
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
            val cachedRepos = localDataSource.getAllRepositories()
            if (cachedRepos.isNotEmpty()) {
                Napier.d("Emitting ${cachedRepos.size} items from local cache", tag = TAG)
                emit(cachedRepos)
            }
        }

        try {
            Napier.d("Fetching page $page from network...", tag = TAG)
            val response = remoteDataSource.searchRepositories(query, page, perPage)
            val networkRepos = response.items.map { it.toDomain() }

            if (forceRefresh || page == 1) {
                Napier.i("Clearing old cache for a fresh start", tag = TAG)
                localDataSource.clearAll()
            }

            localDataSource.insertRepositories(networkRepos)
            emit(networkRepos)

        } catch (e: Exception) {
            Napier.e("Error during searchRepositories", e, tag = TAG)
            val domainError = mapToAppError(e)
            
            if (page == 1) {
                val cachedRepos = localDataSource.getAllRepositories()
                if (cachedRepos.isEmpty()) {
                    Napier.w("No cache available after network error. Propagating exception.", tag = TAG)
                    throw AppErrorException(domainError)
                } else {
                    Napier.w("Network failed, but user sees cached data.", tag = TAG)
                    emit(cachedRepos)
                }
            } else {
                throw AppErrorException(domainError)
            }
        }
    }

    override suspend fun clearLocalData() {
        Napier.i("Clearing all local repository data (Logout/Reset)", tag = TAG)
        localDataSource.clearAll()
    }

    override fun getFavoritesFlow(): Flow<List<GithubRepository>> {
        return localDataSource.getFavoritesFlow()
    }

    override suspend fun starRepository(owner: String, name: String): Result<Unit> {
        return remoteDataSource.starRepository(owner, name)
    }

    override suspend fun unstarRepository(owner: String, name: String): Result<Unit> {
        return remoteDataSource.unstarRepository(owner, name)
    }

    private fun mapToAppError(e: Exception): AppError {
        return when (e) {
            is ClientRequestException -> {
                if (e.response.status.value == 403) AppError.Network // Or more specific rate limit error
                else AppError.Network
            }
            is ServerResponseException -> AppError.Server
            else -> AppError.Unknown(e.message)
        }
    }
}
