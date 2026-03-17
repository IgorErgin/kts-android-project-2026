package com.github.igorergin.ktsandroid.feature.repositories.data.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchResponse(
    @SerialName("total_count") val totalCount: Int,
    @SerialName("items") val items: List<GithubRepositoryDto>
)

@Serializable
data class GithubRepositoryDto(
    val id: Int,
    val name: String,
    @SerialName("full_name") val fullName: String,
    val description: String? = null,
    val language: String? = null,
    @SerialName("stargazers_count") val stargazersCount: Int,
    val owner: GithubOwnerDto
)

@Serializable
data class GithubOwnerDto(
    val login: String,
    @SerialName("avatar_url") val avatarUrl: String
)