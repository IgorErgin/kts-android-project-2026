package com.github.igorergin.ktsandroid.feature.detail.domain.model

sealed class GithubContent {
    abstract val name: String
    abstract val path: String
    abstract val sha: String

    data class File(
        override val name: String,
        override val path: String,
        override val sha: String,
        val size: Long,
        val downloadUrl: String?,
        val content: String? = null,
        val encoding: String? = null
    ) : GithubContent()

    data class Directory(
        override val name: String,
        override val path: String,
        override val sha: String
    ) : GithubContent()
}