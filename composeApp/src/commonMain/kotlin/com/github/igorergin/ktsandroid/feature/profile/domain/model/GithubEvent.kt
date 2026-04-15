package com.github.igorergin.ktsandroid.feature.profile.domain.model

data class GithubEvent(
    val id: String,
    val type: String,
    val actorLogin: String,
    val actorAvatarUrl: String,
    val repoName: String,
    val createdAt: String,
    val payloadAction: String? = null,
    val commitMessages: List<String> = emptyList()
)
