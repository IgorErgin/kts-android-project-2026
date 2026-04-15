package com.github.igorergin.ktsandroid.feature.profile.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GithubEventDto(
    val id: String,
    val type: String,
    val actor: ActorDto,
    val repo: RepoDto,
    val payload: PayloadDto? = null,
    @SerialName("created_at") val createdAt: String
)

@Serializable
data class ActorDto(
    val login: String,
    @SerialName("avatar_url") val avatarUrl: String
)

@Serializable
data class RepoDto(
    val name: String
)

@Serializable
data class PayloadDto(
    val action: String? = null,
    val commits: List<CommitSummaryDto>? = null,
    @SerialName("ref_type") val refType: String? = null
)

@Serializable
data class CommitSummaryDto(
    val message: String,
    val sha: String
)
