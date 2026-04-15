package com.github.igorergin.ktsandroid.core.network

import com.github.igorergin.ktsandroid.core.domain.error.AppError
import com.github.igorergin.ktsandroid.core.domain.error.AppErrorException
import com.github.igorergin.ktsandroid.core.util.SnackbarManager
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import kotlinx.coroutines.CancellationException


suspend inline fun <T> safeApiCall(crossinline block: suspend () -> T): Result<T> {
    return try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: ClientRequestException) {
        val error = if (e.response.status.value == 401) AppError.Unauthorized else AppError.Network
        if (e.response.status.value == 403 || e.response.status.value >= 500) {
            SnackbarManager.showMessage("Network error: ${e.response.status.value}")
        }
        Result.failure(AppErrorException(error))
    } catch (e: ServerResponseException) {
        SnackbarManager.showMessage("Server error: ${e.response.status.value}")
        Result.failure(AppErrorException(AppError.Server))
    } catch (e: kotlinx.io.IOException) {
        Result.failure(AppErrorException(AppError.Network))
    } catch (e: Exception) {
        Result.failure(AppErrorException(AppError.Unknown(e.message)))
    }
}
