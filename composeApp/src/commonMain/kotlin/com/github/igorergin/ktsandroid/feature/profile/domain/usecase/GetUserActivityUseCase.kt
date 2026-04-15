package com.github.igorergin.ktsandroid.feature.profile.domain.usecase

import com.github.igorergin.ktsandroid.feature.profile.data.repository.ProfileRepository
import com.github.igorergin.ktsandroid.feature.profile.domain.model.GithubEvent

class GetUserActivityUseCase(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(username: String): Result<List<GithubEvent>> = 
        repository.getUserActivity(username)
}
