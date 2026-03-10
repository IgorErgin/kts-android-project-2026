package com.github.igorergin.ktsandroid.feature.auth.data.repository

import kotlinx.coroutines.delay

/**
 * Репозиторий для имитации процесса входа.
 */
class LoginRepository {

    suspend fun login(login: String, password: String): Result<Unit> {
        // Имитируем запрос на сервер
        delay(1000)

        // Моковая проверка: пускаем только если логин "admin" и пароль "1234"
        return if (login == "admin" && password == "1234") {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Неверный логин или пароль"))
        }
    }
}