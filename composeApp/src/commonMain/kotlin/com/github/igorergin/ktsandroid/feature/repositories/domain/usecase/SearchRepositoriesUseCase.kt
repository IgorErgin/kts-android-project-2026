package com.github.igorergin.ktsandroid.feature.repositories.domain.usecase

import com.github.igorergin.ktsandroid.feature.repositories.domain.model.GithubRepository
import com.github.igorergin.ktsandroid.feature.repositories.domain.repository.GithubRepoRepository
import kotlinx.coroutines.flow.Flow

/**
 * UseCase для поиска репозиториев. Инкапсулирует бизнес-логику пагинации и поиска.
 */
class SearchRepositoriesUseCase(
    private val repository: GithubRepoRepository
) {
    operator fun invoke(
        query: String,
        page: Int,
        forceRefresh: Boolean = false
    ): Flow<List<GithubRepository>> {
        val searchQuery = if (query.isBlank()) "stars:>1000" else query
        return repository.searchRepositories(searchQuery, page, forceRefresh = forceRefresh)
    }

    fun getFavoritesFlow() = repository.getFavoritesFlow()
}