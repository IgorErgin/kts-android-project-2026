package com.github.igorergin.ktsandroid.core.navigation

import kotlinx.serialization.Serializable


/**
 * Базовый интерфейс для всех маршрутов приложения.
 */
sealed interface Destination {

    @Serializable
    data object Welcome : Destination

    @Serializable
    data object Login : Destination

    @Serializable
    data object Main : Destination

    /**
     * Маршрут с параметрами.
     * Параметры автоматически сериализуются библиотекой навигации.
     */
    @Serializable
    data class Detail(
        val owner: String,
        val repo: String
    ) : Destination
}