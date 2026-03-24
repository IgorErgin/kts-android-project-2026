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