package com.github.igorergin.ktsandroid.feature.repositories.presentation

import com.github.igorergin.ktsandroid.feature.repositories.domain.model.GithubRepository


sealed interface MainIntent {
    data class SearchQueryChanged(val query: String) : MainIntent
    data object Refresh : MainIntent
    data object LoadNextPage : MainIntent
    data class ToggleFavorite(val repo: GithubRepository) : MainIntent
}