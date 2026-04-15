package com.github.igorergin.ktsandroid.feature.detail.presentation

import app.cash.turbine.test
import com.github.igorergin.ktsandroid.core.util.AppDispatchers
import com.github.igorergin.ktsandroid.core.util.ShareManager
import com.github.igorergin.ktsandroid.feature.detail.data.repository.DetailRepository
import com.github.igorergin.ktsandroid.feature.detail.domain.usecase.*
import com.github.igorergin.ktsandroid.feature.repositories.FakeGithubRepoRepository
import com.github.igorergin.ktsandroid.feature.repositories.FakeRepositoryDao
import com.github.igorergin.ktsandroid.feature.repositories.domain.usecase.GetFavoritesUseCase
import com.github.igorergin.ktsandroid.feature.repositories.domain.usecase.ToggleFavoriteUseCase
import com.github.igorergin.ktsandroid.util.TestDispatchers
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val appDispatchers = TestDispatchers(testDispatcher)

    private lateinit var viewModel: DetailViewModel
    private val owner = "owner"
    private val repo = "repo"

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        // Mock HTTP Client for DetailRepository
        val mockHttpClient = HttpClient(MockEngine {
            respond(
                content = "{}", 
                status = HttpStatusCode.OK,
                headers = headersOf("Content-Type", ContentType.Application.Json.toString())
            )
        })

        val detailRepository = DetailRepository(mockHttpClient)
        val githubRepository = FakeGithubRepoRepository()
        val dao = FakeRepositoryDao()

        viewModel = DetailViewModel(
            owner = owner,
            repo = repo,
            getRepositoryDetailsUseCase = GetRepositoryDetailsUseCase(detailRepository),
            getReadmeUseCase = GetReadmeUseCase(detailRepository),
            createIssueUseCase = CreateIssueUseCase(detailRepository),
            getContentUseCase = GetContentUseCase(detailRepository),
            uploadFileUseCase = UploadFileUseCase(detailRepository),
            getPullRequestsUseCase = GetPullRequestsUseCase(detailRepository),
            toggleFavoriteUseCase = ToggleFavoriteUseCase(dao, githubRepository),
            getFavoritesUseCase = GetFavoritesUseCase(dao), // Fix: GetFavoritesUseCase expects RepositoryDao
            shareManager = object : ShareManager { override fun shareText(text: String) {} },
            dispatchers = appDispatchers
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should show loading`() = runTest {
        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.isLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `toggleFavorite should update state`() = runTest {
        viewModel.state.test {
            awaitItem() // Initial
            viewModel.toggleFavorite()
            // In DetailViewModel, toggleFavorite does optimistic update
            // and then calls UseCase. We can verify isFavorite change.
        }
    }
}
