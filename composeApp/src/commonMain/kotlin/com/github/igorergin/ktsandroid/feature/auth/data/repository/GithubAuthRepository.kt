package com.github.igorergin.ktsandroid.feature.auth.data.repository

import com.github.igorergin.ktsandroid.core.datastore.TokenStorage
import com.github.igorergin.ktsandroid.core.network.GithubAuthClient
import kotlinx.coroutines.CancellationException

class GithubAuthRepository {

    private val authClient: GithubAuthClient = GithubAuthClient()
    private val tokenStorage: TokenStorage = TokenStorage
    suspend fun exchangeCodeForToken(code: String): Result<String> {
        return try {
            val response = authClient.exchangeCodeForToken(code)

            if (response.error != null) {
                return Result.failure(
                    Exception(response.errorDescription ?: response.error)
                )
            }

            if (response.accessToken != null) {
                tokenStorage.saveToken(response.accessToken)
                Result.success(response.accessToken)
            } else {
                Result.failure(Exception("Токен не был получен из ответа GitHub"))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }

    }
}