package com.github.igorergin.ktsandroid.feature.repositories.data.mapper

import com.github.igorergin.ktsandroid.feature.repositories.data.local.FavoriteEntity
import com.github.igorergin.ktsandroid.feature.repositories.data.local.RepositoryEntity
import com.github.igorergin.ktsandroid.feature.repositories.data.remote.model.GithubRepoDto
import com.github.igorergin.ktsandroid.feature.repositories.domain.model.GithubRepository
import com.github.igorergin.ktsandroid.feature.repositories.domain.model.RepositoryId


fun GithubRepoDto.toEntity() = RepositoryEntity(
    id = id,
    name = name,
    fullName = fullName,
    description = description ?: "",
    starsCount = starsCount,
    language = language ?: "Unknown",
    ownerName = owner.login,
    ownerAvatarUrl = owner.avatarUrl
)

fun GithubRepoDto.toDomain() = GithubRepository(
    id = RepositoryId(id),
    name = name,
    fullName = fullName,
    description = description ?: "",
    starsCount = starsCount,
    language = language ?: "Unknown",
    ownerName = owner.login,
    ownerAvatarUrl = owner.avatarUrl,
    isFavorite = false
)

fun GithubRepository.toEntity() = RepositoryEntity(
    id = id.value,
    name = name,
    fullName = fullName,
    description = description,
    starsCount = starsCount,
    language = language,
    ownerName = ownerName,
    ownerAvatarUrl = ownerAvatarUrl
)


fun RepositoryEntity.toDomain() = GithubRepository(
    id = RepositoryId(id),
    name = name,
    fullName = fullName,
    description = description,
    starsCount = starsCount,
    language = language,
    ownerName = ownerName,
    ownerAvatarUrl = ownerAvatarUrl,
    isFavorite = false
)

fun FavoriteEntity.toDomain() = GithubRepository(
    id = RepositoryId(id),
    name = name,
    fullName = fullName,
    description = description,
    starsCount = starsCount,
    language = language,
    ownerName = ownerName,
    ownerAvatarUrl = ownerAvatarUrl,
    isFavorite = true
)

fun GithubRepository.toFavoriteEntity() = FavoriteEntity(
    id = id.value,
    name = name,
    fullName = fullName,
    description = description,
    starsCount = starsCount,
    language = language,
    ownerName = ownerName,
    ownerAvatarUrl = ownerAvatarUrl
)
