package com.github.igorergin.ktsandroid.feature.repositories.domain.model

import androidx.compose.runtime.Immutable
import kotlin.jvm.JvmInline

@JvmInline
value class RepositoryId(val value: Long)

@Immutable
data class GithubRepository(
    val id: RepositoryId,
    val name: String,
    val fullName: String,
    val description: String,
    val language: String,
    val starsCount: Int,
    val ownerName: String,
    val ownerAvatarUrl: String,
    val isFavorite: Boolean = false
)
