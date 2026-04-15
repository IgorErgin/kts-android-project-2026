package com.github.igorergin.ktsandroid.feature.profile.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserProfileDto(
    val id: String,
    val login: String,
    val name: String? = null,
    @SerialName("avatar_url") val avatarUrl: String,
    val bio: String? = null,
    @SerialName("followers") val followersCount: Int? = 0,
    @SerialName("public_repos") val publicReposCount: Int? = 0
)