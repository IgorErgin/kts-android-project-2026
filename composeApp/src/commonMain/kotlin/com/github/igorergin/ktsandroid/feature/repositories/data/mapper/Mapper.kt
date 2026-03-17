package com.github.igorergin.ktsandroid.feature.repositories.data.mapper

import com.github.igorergin.ktsandroid.feature.repositories.data.local.RepositoryEntity
import com.github.igorergin.ktsandroid.feature.repositories.data.remote.model.GithubRepoDto
import com.github.igorergin.ktsandroid.feature.repositories.domain.model.GithubRepository


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


fun RepositoryEntity.toDomain() = GithubRepository(
    id = id,
    name = name,
    fullName = fullName,
    description = description,
    starsCount = starsCount,
    language = language,
    ownerName = ownerName,
    ownerAvatarUrl = ownerAvatarUrl
)