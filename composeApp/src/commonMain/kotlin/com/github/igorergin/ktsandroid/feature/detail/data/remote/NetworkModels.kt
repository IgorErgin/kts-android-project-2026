package com.github.igorergin.ktsandroid.feature.detail.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReadmeResponse(
    val content: String,
    val encoding: String,
    @SerialName("download_url") val downloadUrl: String? = null
)

@Serializable
data class CreateIssueRequest(
    val title: String,
    val body: String
)

@Serializable
data class IssueResponse(
    val id: Long,
    val number: Int,
    @SerialName("html_url") val htmlUrl: String
)

@Serializable
data class GithubContentDto(
    val name: String,
    val path: String,
    val sha: String,
    val size: Long,
    val type: String,
    @SerialName("download_url") val downloadUrl: String? = null,
    val content: String? = null,
    val encoding: String? = null
)

@Serializable
data class UploadFileRequest(
    val message: String,
    val content: String,
    val sha: String? = null,
    val branch: String? = null
)

@Serializable
data class UploadFileResponse(
    val content: GithubContentDto?,
    val commit: CommitDto
)

@Serializable
data class CommitDto(
    val sha: String,
    @SerialName("html_url") val htmlUrl: String
)

@Serializable
data class PullRequestDto(
    val id: Long,
    val number: Int,
    val state: String,
    val title: String,
    val body: String? = null,
    @SerialName("html_url") val htmlUrl: String,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String,
    val user: UserShortDto
)

@Serializable
data class UserShortDto(
    val login: String,
    @SerialName("avatar_url") val avatarUrl: String
)
