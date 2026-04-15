package com.github.igorergin.ktsandroid.feature.detail.domain.usecase

import com.github.igorergin.ktsandroid.feature.detail.data.repository.DetailRepository
import com.github.igorergin.ktsandroid.feature.detail.domain.model.GithubContent

class GetContentUseCase(
    private val repository: DetailRepository
) {
    suspend operator fun invoke(owner: String, repo: String, path: String = ""): Result<List<GithubContent>> {
        return repository.getContent(owner, repo, path)
    }
}