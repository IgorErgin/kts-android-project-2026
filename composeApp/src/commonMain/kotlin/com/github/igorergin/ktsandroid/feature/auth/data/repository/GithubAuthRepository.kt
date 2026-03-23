package com.github.igorergin.ktsandroid.feature.auth.data.repository

import com.github.igorergin.ktsandroid.core.datastore.TokenManager
import com.github.igorergin.ktsandroid.core.network.safeApiCall
import com.github.igorergin.ktsandroid.feature.auth.data.api.AuthApi
import com.github.igorergin.ktsandroid.feature.repositories.domain.repository.GithubRepoRepository

class GithubAuthRepository(
    private val authApi: AuthApi,
    private val tokenManager: TokenManager,
    private val githubRepoRepository: GithubRepoRepository
) {

    suspend fun exchangeCodeForToken(code: String): Result<String> = safeApiCall {
        val response = authApi.exchangeCodeForToken(code)

        if (response.error != null) {
            throw Exception(response.errorDescription ?: response.error)
        }

        val token = response.accessToken ?: throw Exception("Токен не был получен из ответа GitHub")

        tokenManager.saveTokens(
            accessToken = token,
            refreshToken = response.refreshToken
        )
        token
    }

    suspend fun logout(): Result<Unit> = safeApiCall {
        githubRepoRepository.clearLocalData()
        tokenManager.clear()
    }
}