package com.github.igorergin.ktsandroid.feature.detail.domain.usecase

import com.github.igorergin.ktsandroid.feature.detail.data.repository.DetailRepository

class GetReadmeUseCase(private val repository: DetailRepository) {
    suspend operator fun invoke(owner: String, repo: String) = repository.getReadme(owner, repo)
}