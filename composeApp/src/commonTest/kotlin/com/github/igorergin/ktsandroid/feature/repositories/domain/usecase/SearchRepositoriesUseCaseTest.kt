package com.github.igorergin.ktsandroid.feature.repositories.domain.usecase

import com.github.igorergin.ktsandroid.feature.repositories.FakeGithubRepoRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SearchRepositoriesUseCaseTest {

    private val repository = FakeGithubRepoRepository()
    private val useCase = SearchRepositoriesUseCase(repository)

    @Test
    fun `when query is blank should use default stars query`() = runTest {
        // When
        useCase.invoke(query = "", page = 1).first()

        // Then
        assertEquals("stars:>1000", repository.lastQuery)
    }

    @Test
    fun `when query is provided should pass it to repository`() = runTest {
        // When
        val query = "kotlin"
        useCase.invoke(query = query, page = 1).first()

        // Then
        assertEquals(query, repository.lastQuery)
    }
}
