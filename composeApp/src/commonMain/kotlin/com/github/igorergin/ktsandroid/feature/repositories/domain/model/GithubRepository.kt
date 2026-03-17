package com.github.igorergin.ktsandroid.feature.repositories.domain.model

data class GithubRepository(
    val id: Long,
    val name: String,
    val fullName: String,
    val description: String,
    val language: String,
    val starsCount: Int,
    val ownerName: String,
    val ownerAvatarUrl: String
)