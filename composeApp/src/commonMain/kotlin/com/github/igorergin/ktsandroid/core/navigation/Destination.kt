package com.github.igorergin.ktsandroid.core.navigation

import kotlinx.serialization.Serializable

sealed interface Destination {

    @Serializable
    data object Splash : Destination

    @Serializable
    data object Welcome : Destination

    @Serializable
    data object Login : Destination

    @Serializable
    data object Main : Destination

    @Serializable
    data object Profile : Destination

    @Serializable
    data class Detail(
        val owner: String,
        val repo: String
    ) : Destination
}