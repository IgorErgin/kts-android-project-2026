package com.github.igorergin.ktsandroid.feature.repositories

import com.github.igorergin.ktsandroid.feature.detail.domain.usecase.GetRepositoryDetailsUseCase
import com.github.igorergin.ktsandroid.feature.repositories.data.local.FavoriteEntity
import com.github.igorergin.ktsandroid.feature.repositories.data.local.GithubLocalDataSource
import com.github.igorergin.ktsandroid.feature.repositories.data.local.RepositoryDao
import com.github.igorergin.ktsandroid.feature.repositories.data.local.RepositoryEntity
import com.github.igorergin.ktsandroid.feature.repositories.data.remote.GithubRemoteDataSource
import com.github.igorergin.ktsandroid.feature.repositories.data.remote.model.GithubSearchResponse
import com.github.igorergin.ktsandroid.feature.repositories.domain.model.GithubRepository
import com.github.igorergin.ktsandroid.feature.repositories.domain.model.RepositoryId
import com.github.igorergin.ktsandroid.feature.repositories.domain.repository.GithubRepoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf

class FakeGithubRepoRepository : GithubRepoRepository {
    var lastQuery: String = ""
    var searchResult: List<GithubRepository> = emptyList()
    val favoritesFlow = MutableStateFlow<List<GithubRepository>>(emptyList())
    var starCount = 0
    var unstarCount = 0

    override fun searchRepositories(query: String, page: Int, perPage: Int, forceRefresh: Boolean): Flow<List<GithubRepository>> {
        lastQuery = query
        return flowOf(searchResult)
    }

    override fun getFavoritesFlow(): Flow<List<GithubRepository>> = favoritesFlow
    override suspend fun starRepository(owner: String, name: String): Result<Unit> {
        starCount++
        return Result.success(Unit)
    }
    override suspend fun unstarRepository(owner: String, name: String): Result<Unit> {
        unstarCount++
        return Result.success(Unit)
    }
    override suspend fun clearLocalData() {}
}

class FakeRepositoryDao : RepositoryDao {
    var localRepos = mutableListOf<RepositoryEntity>()
    var favoriteEntities = mutableListOf<FavoriteEntity>()

    override suspend fun getAllRepositories(): List<RepositoryEntity> = localRepos.toList()
    override suspend fun insertRepositories(repositories: List<RepositoryEntity>) { localRepos.addAll(repositories) }
    override suspend fun clearAll() { localRepos.clear() }
    override fun getFavoritesFlow(): Flow<List<FavoriteEntity>> = flowOf(favoriteEntities.toList())
    override suspend fun isFavorite(id: Long): Boolean = favoriteEntities.any { it.id == id }
    override suspend fun insertFavorite(favorite: FavoriteEntity) { favoriteEntities.add(favorite) }
    override suspend fun deleteFavorite(favorite: FavoriteEntity) { favoriteEntities.removeAll { it.id == favorite.id } }
}

class FakeRemoteDataSource : GithubRemoteDataSource {
    var searchResponse = GithubSearchResponse(items = emptyList())
    var shouldThrowError = false

    override suspend fun searchRepositories(
        query: String,
        page: Int,
        perPage: Int
    ): GithubSearchResponse {
        if (shouldThrowError) throw Exception("Network error")
        return searchResponse
    }
    override suspend fun starRepository(owner: String, name: String): Result<Unit> = Result.success(Unit)
    override suspend fun unstarRepository(owner: String, name: String): Result<Unit> = Result.success(Unit)
}

class FakeLocalDataSource : GithubLocalDataSource {
    var repos = mutableListOf<GithubRepository>()
    override suspend fun getAllRepositories(): List<GithubRepository> = repos.toList()
    override suspend fun insertRepositories(repositories: List<GithubRepository>) { repos.addAll(repositories) }
    override suspend fun clearAll() { repos.clear() }
    override fun getFavoritesFlow(): Flow<List<GithubRepository>> = flowOf(emptyList())
}

fun createFakeRepo(id: Long, isFavorite: Boolean = false) = GithubRepository(
    id = RepositoryId(id),
    name = "Repo $id",
    fullName = "Owner/Repo $id",
    description = "Description",
    language = "Kotlin",
    starsCount = 100,
    ownerName = "Owner",
    ownerAvatarUrl = "",
    isFavorite = isFavorite
)
