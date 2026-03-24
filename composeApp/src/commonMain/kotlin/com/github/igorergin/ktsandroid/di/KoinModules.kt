package com.github.igorergin.ktsandroid.di

import com.github.igorergin.ktsandroid.core.network.NetworkClient
import com.github.igorergin.ktsandroid.core.util.AppDispatchers
import com.github.igorergin.ktsandroid.core.util.AppDispatchersImpl
import com.github.igorergin.ktsandroid.feature.auth.data.api.AuthApi
import com.github.igorergin.ktsandroid.feature.auth.data.api.AuthApiImpl
import com.github.igorergin.ktsandroid.feature.auth.data.repository.GithubAuthRepository
import com.github.igorergin.ktsandroid.feature.auth.presentation.LoginViewModel
import com.github.igorergin.ktsandroid.feature.detail.data.repository.DetailRepository
import com.github.igorergin.ktsandroid.feature.detail.domain.usecase.CreateIssueUseCase
import com.github.igorergin.ktsandroid.feature.detail.domain.usecase.GetReadmeUseCase
import com.github.igorergin.ktsandroid.feature.detail.domain.usecase.GetRepositoryDetailsUseCase
import com.github.igorergin.ktsandroid.feature.detail.presentation.DetailViewModel
import com.github.igorergin.ktsandroid.feature.profile.data.repository.ProfileRepository
import com.github.igorergin.ktsandroid.feature.profile.presentation.ProfileViewModel
import com.github.igorergin.ktsandroid.feature.repositories.data.repository.GithubRepoRepositoryImpl
import com.github.igorergin.ktsandroid.feature.repositories.domain.repository.GithubRepoRepository
import com.github.igorergin.ktsandroid.feature.repositories.domain.usecase.GetFavoritesUseCase
import com.github.igorergin.ktsandroid.feature.repositories.domain.usecase.SearchRepositoriesUseCase
import com.github.igorergin.ktsandroid.feature.repositories.domain.usecase.ToggleFavoriteUseCase
import com.github.igorergin.ktsandroid.feature.repositories.presentation.MainViewModel
import com.github.igorergin.ktsandroid.feature.repositories.presentation.favorites.FavoritesViewModel
import com.github.igorergin.ktsandroid.feature.splash.presentation.SplashViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

val coreModule = module {
    singleOf(::AppDispatchersImpl) bind AppDispatchers::class

    // Сеть
    single(named("unauthenticated")) { NetworkClient.unauthenticatedClient }
    single<AuthApi> { AuthApiImpl(get(named("unauthenticated"))) }
    single { NetworkClient.createHttpClient(get(), get()) }
}

val repositoryModule = module {
    singleOf(::GithubAuthRepository)
    singleOf(::ProfileRepository)
    singleOf(::DetailRepository)
    singleOf(::GithubRepoRepositoryImpl) bind GithubRepoRepository::class
}

val useCaseModule = module {
    // Репозитории
    factoryOf(::SearchRepositoriesUseCase)
    factoryOf(::ToggleFavoriteUseCase)
    factoryOf(::GetFavoritesUseCase)

    // Детализация
    factoryOf(::GetRepositoryDetailsUseCase)
    factoryOf(::GetReadmeUseCase)
    factoryOf(::CreateIssueUseCase)
}

val viewModelModule = module {
    viewModelOf(::SplashViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::MainViewModel)
    viewModelOf(::ProfileViewModel)
    viewModelOf(::FavoritesViewModel)

    factory { params ->
        DetailViewModel(
            owner = params.get(),
            repo = params.get(),
            getRepositoryDetailsUseCase = get(),
            getReadmeUseCase = get(),
            createIssueUseCase = get(),
            shareManager = get(),
            dispatchers = get()
        )
    }
}

fun appModules() = listOf(coreModule, repositoryModule, useCaseModule, viewModelModule)