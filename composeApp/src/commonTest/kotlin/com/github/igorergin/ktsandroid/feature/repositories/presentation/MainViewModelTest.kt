package com.github.igorergin.ktsandroid.feature.repositories.presentation

import app.cash.turbine.test
import com.github.igorergin.ktsandroid.feature.repositories.FakeGithubRepoRepository
import com.github.igorergin.ktsandroid.feature.repositories.createFakeRepo
import com.github.igorergin.ktsandroid.feature.repositories.domain.usecase.SearchRepositoriesUseCase
import com.github.igorergin.ktsandroid.feature.repositories.domain.usecase.ToggleFavoriteUseCase
import com.github.igorergin.ktsandroid.util.TestDispatchers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    private lateinit var repository: FakeGithubRepoRepository
    private lateinit var dispatchers: TestDispatchers
    private lateinit var searchUseCase: SearchRepositoriesUseCase
    private lateinit var viewModel: MainViewModel

    @BeforeTest
    fun setup() {
        val testDispatcher = dispatchers().testDispatcher
        Dispatchers.setMain(testDispatcher)
        
        repository = FakeGithubRepoRepository()
        dispatchers = TestDispatchers(testDispatcher)
        searchUseCase = SearchRepositoriesUseCase(repository)
        
        val toggleUseCase = ToggleFavoriteUseCase(
            com.github.igorergin.ktsandroid.feature.repositories.FakeRepositoryDao(),
            repository
        )

        viewModel = MainViewModel(searchUseCase, toggleUseCase, dispatchers)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun dispatchers() = TestDispatchers()

    @Test
    fun `initial state should have empty query`() = runTest {
        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.query.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `SearchQueryChanged intent should update state query`() = runTest {
        val newQuery = "compose"
        
        viewModel.state.test {
            // Пропускаем начальное состояние
            awaitItem() 
            
            viewModel.handleIntent(MainIntent.SearchQueryChanged(newQuery))
            
            val state = awaitItem()
            assertEquals(newQuery, state.query)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when repository returns data state should be updated`() = runTest {
        val fakeData = listOf(createFakeRepo(1), createFakeRepo(2))
        repository.searchResult = fakeData
        
        viewModel.state.test {
            // 1. Начальное состояние
            awaitItem() 
            
            viewModel.handleIntent(MainIntent.Refresh)
            
            // 2. Состояние обновления (isRefreshing = true)
            val refreshingState = awaitItem()
            assertTrue(refreshingState.isRefreshing)
            
            // 3. Состояние с данными (isRefreshing = false + repos)
            val dataState = awaitItem()
            assertEquals(fakeData.size, dataState.repositories.size)
            assertEquals(fakeData[0].id, dataState.repositories[0].id)

            cancelAndIgnoreRemainingEvents()
        }
    }
}
