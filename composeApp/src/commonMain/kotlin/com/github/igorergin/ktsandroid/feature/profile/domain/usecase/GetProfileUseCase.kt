package com.github.igorergin.ktsandroid.feature.profile.domain.usecase

import com.github.igorergin.ktsandroid.feature.profile.data.repository.ProfileRepository
import com.github.igorergin.ktsandroid.feature.profile.domain.model.UserProfile

class GetProfileUseCase(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(): Result<UserProfile> = repository.getCurrentUser()
}
