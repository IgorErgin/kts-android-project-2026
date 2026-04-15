package com.github.igorergin.ktsandroid.feature.detail.domain.usecase

import com.github.igorergin.ktsandroid.feature.detail.data.repository.DetailRepository
import com.github.igorergin.ktsandroid.feature.detail.domain.model.PullRequest

class GetPullRequestsUseCase(
    private val repository: DetailRepository
) {
    suspend operator fun invoke(owner: String, repo: String): Result<List<PullRequest>> {
        return repository.getPullRequests(owner, repo)
    }
}
