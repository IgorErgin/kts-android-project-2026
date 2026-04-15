package com.github.igorergin.ktsandroid.feature.repositories.data.repository

import app.cash.turbine.test
import com.github.igorergin.ktsandroid.feature.repositories.FakeLocalDataSource
import com.github.igorergin.ktsandroid.feature.repositories.FakeRemoteDataSource
import com.github.igorergin.ktsandroid.feature.repositories.createFakeRepo
import com.github.igorergin.ktsandroid.feature.repositories.data.remote.model.GithubOwnerDto
import com.github.igorergin.ktsandroid.feature.repositories.data.remote.model.GithubRepoDto
import com.github.igorergin.ktsandroid.feature.repositories.data.remote.model.GithubSearchResponse
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GithubRepoRepositoryImplTest {

    private lateinit var remoteDataSource: FakeRemoteDataSource
    private lateinit var localDataSource: FakeLocalDataSource
    private lateinit var repository: GithubRepoRepositoryImpl

    @BeforeTest
    fun setup() {
        remoteDataSource = FakeRemoteDataSource()
        localDataSource = FakeLocalDataSource()
        repository = GithubRepoRepositoryImpl(remoteDataSource, localDataSource)
    }

    @Test
    fun `searchRepositories should emit cached data first then network data if page is 1`() = runTest {
        // Given
        val cachedRepo = createFakeRepo(1)
        val networkRepo = createFakeRepo(2)
        
        localDataSource.repos.add(cachedRepo)
        
        // Настраиваем удаленный источник
        remoteDataSource.searchResponse = GithubSearchResponse(
            items = listOf(GithubRepoDto(
                id = networkRepo.id.value,
                name = networkRepo.name,
                fullName = networkRepo.fullName,
                description = networkRepo.description,
                starsCount = networkRepo.starsCount,
                language = networkRepo.language,
                owner = GithubOwnerDto(
                    login = networkRepo.ownerName,
                    avatarUrl = networkRepo.ownerAvatarUrl
                )
            ))
        )

        // When
        repository.searchRepositories(query = "test", page = 1).test {
            // Then: First emission from cache
            val firstEmission = awaitItem()
            assertEquals(1, firstEmission.size, "First emission should be from cache")
            assertEquals(cachedRepo.id, firstEmission[0].id)
            
            // Then: Second emission from network
            val secondEmission = awaitItem()
            assertEquals(1, secondEmission.size, "Second emission should be from network")
            assertEquals(networkRepo.id, secondEmission[0].id)
            
            awaitComplete()
        }
    }

    @Test
    fun `searchRepositories should emit cached data and then error if network fails and cache is empty`() = runTest {
        // Given
        localDataSource.repos.clear()
        remoteDataSource.shouldThrowError = true

        // When
        repository.searchRepositories(query = "test", page = 1).test {
            // Then: Should throw exception since no cache is available
            val error = awaitError()
            assertTrue(error is com.github.igorergin.ktsandroid.core.domain.error.AppErrorException)
        }
    }

    @Test
    fun `searchRepositories should emit cache and then keep it if network fails but cache exists`() = runTest {
        // Given
        val cachedRepo = createFakeRepo(1)
        localDataSource.repos.add(cachedRepo)
        remoteDataSource.shouldThrowError = true

        // When
        repository.searchRepositories(query = "test", page = 1).test {
            // First emission from cache
            val first = awaitItem()
            assertEquals(1, first.size)

            // Since network fails but we HAVE cache, the repo implementation 
            // currently emits cache AGAIN in the catch block (per line 61 of GithubRepoRepositoryImpl)
            val second = awaitItem()
            assertEquals(1, second.size)
            assertEquals(cachedRepo.id, second[0].id)

            awaitComplete()
        }
    }
}
