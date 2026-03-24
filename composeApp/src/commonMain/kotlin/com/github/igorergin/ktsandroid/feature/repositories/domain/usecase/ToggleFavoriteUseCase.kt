package com.github.igorergin.ktsandroid.feature.repositories.domain.usecase

import com.github.igorergin.ktsandroid.feature.repositories.data.local.FavoriteEntity
import com.github.igorergin.ktsandroid.feature.repositories.data.local.RepositoryDao
import com.github.igorergin.ktsandroid.feature.repositories.domain.model.GithubRepository

class ToggleFavoriteUseCase(
    private val dao: RepositoryDao
) {
    suspend operator fun invoke(repo: GithubRepository) {
        val isFav = dao.isFavorite(repo.id)
        if (isFav) {
            dao.deleteFavorite(repo.toFavoriteEntity())
        } else {
            dao.insertFavorite(repo.toFavoriteEntity())
        }
    }

    private fun GithubRepository.toFavoriteEntity() = FavoriteEntity(
        id = id,
        name = name,
        fullName = fullName,
        description = description,
        starsCount = starsCount,
        language = language,
        ownerName = ownerName,
        ownerAvatarUrl = ownerAvatarUrl
    )
}