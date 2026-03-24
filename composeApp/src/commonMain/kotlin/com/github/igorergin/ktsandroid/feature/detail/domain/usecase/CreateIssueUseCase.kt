package com.github.igorergin.ktsandroid.feature.detail.domain.usecase

import com.github.igorergin.ktsandroid.feature.detail.data.repository.DetailRepository

class CreateIssueUseCase(private val repository: DetailRepository) {
    suspend operator fun invoke(owner: String, repo: String, title: String, body: String) =
        repository.createIssue(owner, repo, title, body)
}