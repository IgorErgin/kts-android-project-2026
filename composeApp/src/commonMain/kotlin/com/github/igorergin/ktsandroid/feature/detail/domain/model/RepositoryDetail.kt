package com.github.igorergin.ktsandroid.feature.detail.domain.model

/**
 * Расширенная модель деталей репозитория.
 */
data class RepositoryDetail(
    val id: Int,
    val name: String,
    val fullName: String,
    val description: String,
    val starsCount: Int,
    val forksCount: Int,
    val openIssuesCount: Int,
    val ownerLogin: String,
    val ownerAvatarUrl: String,
    val htmlUrl: String,
    val language: String?
)