package com.github.igorergin.ktsandroid.feature.repositories.domain.usecase

import com.github.igorergin.ktsandroid.feature.repositories.data.local.RepositoryDao
import com.github.igorergin.ktsandroid.feature.repositories.data.mapper.toDomain
import com.github.igorergin.ktsandroid.feature.repositories.domain.model.GithubRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetFavoritesUseCase(
    private val dao: RepositoryDao
) {
    operator fun invoke(): Flow<List<GithubRepository>> {
        return dao.getFavoritesFlow().map { entities ->
            entities.map { it.toDomain() }
        }
    }
}
