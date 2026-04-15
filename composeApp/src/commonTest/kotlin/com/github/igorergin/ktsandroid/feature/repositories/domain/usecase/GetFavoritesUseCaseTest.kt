package com.github.igorergin.ktsandroid.feature.repositories.domain.usecase

import app.cash.turbine.test
import com.github.igorergin.ktsandroid.feature.repositories.FakeRepositoryDao
import com.github.igorergin.ktsandroid.feature.repositories.data.local.FavoriteEntity
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetFavoritesUseCaseTest {

    private lateinit var dao: FakeRepositoryDao
    private lateinit var useCase: GetFavoritesUseCase

    @BeforeTest
    fun setup() {
        dao = FakeRepositoryDao()
        useCase = GetFavoritesUseCase(dao)
    }

    @Test
    fun `invoke should return favorites from DAO mapped to domain`() = runTest {
        // Given
        val favorite = FavoriteEntity(
            id = 1L,
            name = "Repo",
            fullName = "Owner/Repo",
            description = "Desc",
            starsCount = 10,
            language = "Kotlin",
            ownerName = "Owner",
            ownerAvatarUrl = "url"
        )
        dao.insertFavorite(favorite)

        // When & Then
        useCase().test {
            val list = awaitItem()
            assertEquals(1, list.size)
            assertEquals(1L, list[0].id.value)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
