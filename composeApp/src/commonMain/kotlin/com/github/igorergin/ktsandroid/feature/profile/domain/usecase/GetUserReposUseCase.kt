package com.github.igorergin.ktsandroid.feature.profile.domain.usecase

import com.github.igorergin.ktsandroid.feature.profile.data.repository.ProfileRepository
import com.github.igorergin.ktsandroid.feature.repositories.domain.model.GithubRepository

class GetUserReposUseCase(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(): Result<List<GithubRepository>> = repository.getUserRepos()
}
