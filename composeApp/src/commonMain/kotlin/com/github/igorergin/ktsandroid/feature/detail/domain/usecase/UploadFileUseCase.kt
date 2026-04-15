package com.github.igorergin.ktsandroid.feature.detail.domain.usecase

import com.github.igorergin.ktsandroid.feature.detail.data.remote.UploadFileResponse
import com.github.igorergin.ktsandroid.feature.detail.data.repository.DetailRepository

class UploadFileUseCase(
    private val repository: DetailRepository
) {
    suspend operator fun invoke(
        owner: String,
        repo: String,
        path: String,
        message: String,
        contentBase64: String,
        sha: String? = null
    ): Result<UploadFileResponse> {
        return repository.uploadFile(owner, repo, path, message, contentBase64, sha)
    }
}