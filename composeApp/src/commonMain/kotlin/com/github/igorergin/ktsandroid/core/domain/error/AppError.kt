package com.github.igorergin.ktsandroid.core.domain.error


sealed interface AppError {
    data object Network : AppError
    data object Unauthorized : AppError
    data object Server : AppError
    data class Unknown(val message: String?) : AppError
}

class AppErrorException(val error: AppError) : Exception()