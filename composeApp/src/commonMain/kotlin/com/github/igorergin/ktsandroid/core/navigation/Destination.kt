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
    data object MainContainer : Destination

    @Serializable
    data object SearchTab : Destination

    @Serializable
    data object FavoritesTab : Destination

    @Serializable
    data object ProfileTab : Destination

    @Serializable
    data class Detail(
        val owner: String,
        val repo: String
    ) : Destination
}