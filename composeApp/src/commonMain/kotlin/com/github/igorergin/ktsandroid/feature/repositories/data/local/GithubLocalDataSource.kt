package com.github.igorergin.ktsandroid.feature.repositories.data.local

import com.github.igorergin.ktsandroid.feature.repositories.data.mapper.toDomain
import com.github.igorergin.ktsandroid.feature.repositories.data.mapper.toEntity
import com.github.igorergin.ktsandroid.feature.repositories.domain.model.GithubRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface GithubLocalDataSource {
    suspend fun getAllRepositories(): List<GithubRepository>
    suspend fun insertRepositories(repositories: List<GithubRepository>)
    suspend fun clearAll()
    fun getFavoritesFlow(): Flow<List<GithubRepository>>
}

class GithubLocalDataSourceImpl(
    private val repositoryDao: RepositoryDao
) : GithubLocalDataSource {

    override suspend fun getAllRepositories(): List<GithubRepository> {
        return repositoryDao.getAllRepositories().map { it.toDomain() }
    }

    override suspend fun insertRepositories(repositories: List<GithubRepository>) {
        repositoryDao.insertRepositories(repositories.map { it.toEntity() })
    }

    override suspend fun clearAll() {
        repositoryDao.clearAll()
    }

    override fun getFavoritesFlow(): Flow<List<GithubRepository>> {
        return repositoryDao.getFavoritesFlow().map { entities ->
            entities.map { it.toDomain() }
        }
    }
}
