package com.github.igorergin.ktsandroid.feature.detail.domain.usecase

import com.github.igorergin.ktsandroid.feature.detail.data.repository.DetailRepository
import com.github.igorergin.ktsandroid.feature.detail.domain.model.RepositoryDetail

class GetRepositoryDetailsUseCase(
    private val repository: DetailRepository
) {
    suspend operator fun invoke(owner: String, repo: String): Result<RepositoryDetail> {
        return repository.getRepositoryDetails(owner, repo)
    }
}