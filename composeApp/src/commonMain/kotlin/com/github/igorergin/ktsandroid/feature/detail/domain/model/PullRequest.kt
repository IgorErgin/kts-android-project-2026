package com.github.igorergin.ktsandroid.feature.detail.domain.model

data class PullRequest(
    val id: Long,
    val number: Int,
    val state: String,
    val title: String,
    val body: String,
    val htmlUrl: String,
    val createdAt: String,
    val updatedAt: String,
    val userLogin: String,
    val userAvatarUrl: String
)
