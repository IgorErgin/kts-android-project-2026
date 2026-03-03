package com.github.igorergin.ktsandroid.domain.model

/**
 * Модель данных для поста.
 * Вынесена в пакет domain.model, чтобы быть доступной и во ViewModel, и в UI-компонентах.
 */
data class Post(
    val id: Int,
    val text: String,
    val date: String
)