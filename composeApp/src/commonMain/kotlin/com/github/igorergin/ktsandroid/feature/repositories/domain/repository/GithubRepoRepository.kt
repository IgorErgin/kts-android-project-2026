package com.github.igorergin.ktsandroid.feature.repositories.domain.repository

import com.github.igorergin.ktsandroid.feature.repositories.domain.model.GithubRepository
import kotlinx.coroutines.flow.Flow

interface GithubRepoRepository {
    fun searchRepositories(
        query: String,
        page: Int,
        perPage: Int = 20,
        forceRefresh: Boolean = false
    ): Flow<List<GithubRepository>>

    fun getFavoritesFlow(): Flow<List<GithubRepository>>

    suspend fun starRepository(owner: String, name: String): Result<Unit>
    suspend fun unstarRepository(owner: String, name: String): Result<Unit>

    suspend fun clearLocalData()
}