package com.github.igorergin.ktsandroid.feature.repositories.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GithubSearchResponse(
    @SerialName("total_count") val totalCount: Int? = 0,
    @SerialName("items") val items: List<GithubRepoDto>
)

@Serializable
data class GithubRepoDto(
    @SerialName("id") val id: Long,
    @SerialName("name") val name: String,
    @SerialName("full_name") val fullName: String,
    @SerialName("description") val description: String? = null,
    @SerialName("stargazers_count") val starsCount: Int,
    @SerialName("language") val language: String? = null,
    @SerialName("owner") val owner: GithubOwnerDto
)

@Serializable
data class GithubOwnerDto(
    @SerialName("login") val login: String,
    @SerialName("avatar_url") val avatarUrl: String
)