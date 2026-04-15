package com.github.igorergin.ktsandroid.feature.repositories.domain.usecase

import com.github.igorergin.ktsandroid.feature.repositories.data.local.RepositoryDao
import com.github.igorergin.ktsandroid.feature.repositories.data.mapper.toFavoriteEntity
import com.github.igorergin.ktsandroid.feature.repositories.domain.model.GithubRepository
import com.github.igorergin.ktsandroid.feature.repositories.domain.repository.GithubRepoRepository

class ToggleFavoriteUseCase(
    private val dao: RepositoryDao,
    private val repository: GithubRepoRepository
) {
    suspend operator fun invoke(repo: GithubRepository): Result<Unit> {
        val isFav = dao.isFavorite(repo.id.value)
        return if (isFav) {
            repository.unstarRepository(repo.ownerName, repo.name).onSuccess {
                dao.deleteFavorite(repo.toFavoriteEntity())
            }
        } else {
            repository.starRepository(repo.ownerName, repo.name).onSuccess {
                dao.insertFavorite(repo.toFavoriteEntity())
            }
        }
    }
}
