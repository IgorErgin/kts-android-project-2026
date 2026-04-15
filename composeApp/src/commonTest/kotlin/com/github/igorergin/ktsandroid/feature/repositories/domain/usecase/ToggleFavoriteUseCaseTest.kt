package com.github.igorergin.ktsandroid.feature.repositories.domain.usecase

import com.github.igorergin.ktsandroid.feature.repositories.FakeGithubRepoRepository
import com.github.igorergin.ktsandroid.feature.repositories.FakeRepositoryDao
import com.github.igorergin.ktsandroid.feature.repositories.createFakeRepo
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ToggleFavoriteUseCaseTest {

    private lateinit var dao: FakeRepositoryDao
    private lateinit var repository: FakeGithubRepoRepository
    private lateinit var useCase: ToggleFavoriteUseCase

    @BeforeTest
    fun setup() {
        dao = FakeRepositoryDao()
        repository = FakeGithubRepoRepository()
        useCase = ToggleFavoriteUseCase(dao, repository)
    }

    @Test
    fun `invoke should star and save to local when NOT favorite`() = runTest {
        // Given
        val repo = createFakeRepo(1, isFavorite = false)
        // Ensure DAO says it's not favorite
        // (FakeRepositoryDao starts empty, so isFavorite will be false)

        // When
        val result = useCase(repo)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(1, repository.starCount, "Should call starRepository")
        assertEquals(1, dao.favoriteEntities.size, "Should insert to local DAO")
        assertEquals(repo.id.value, dao.favoriteEntities[0].id)
    }

    @Test
    fun `invoke should unstar and remove from local when ALREADY favorite`() = runTest {
        // Given
        val repo = createFakeRepo(1, isFavorite = true)
        // Pre-fill DAO
        dao.insertFavorite(
            com.github.igorergin.ktsandroid.feature.repositories.data.local.FavoriteEntity(
                id = repo.id.value,
                name = repo.name,
                fullName = repo.fullName,
                description = repo.description,
                starsCount = repo.starsCount,
                language = repo.language ?: "",
                ownerName = repo.ownerName,
                ownerAvatarUrl = repo.ownerAvatarUrl
            )
        )

        // When
        val result = useCase(repo)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(1, repository.unstarCount, "Should call unstarRepository")
        assertTrue(dao.favoriteEntities.isEmpty(), "Should remove from local DAO")
    }
}
