package com.github.igorergin.ktsandroid.feature.repositories.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey
    val id: Long,
    val name: String,
    val fullName: String,
    val description: String,
    val starsCount: Int,
    val language: String,
    val ownerName: String,
    val ownerAvatarUrl: String
)