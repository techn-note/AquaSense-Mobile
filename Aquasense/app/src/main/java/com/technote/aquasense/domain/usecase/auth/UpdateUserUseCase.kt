package com.technote.aquasense.domain.usecase.auth

import com.technote.aquasense.data.model.User
import com.technote.aquasense.domain.repository.AuthRepository
import javax.inject.Inject

class UpdateUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend fun updateProfile(name: String? = null, email: String? = null, age: Int? = null, country: String? = null): Result<User> {
        return authRepository.updateUserProfile(name, email, age, country)
    }
    
    suspend fun updatePassword(password: String): Result<User> {
        return authRepository.updateUserPassword(password)
    }
}