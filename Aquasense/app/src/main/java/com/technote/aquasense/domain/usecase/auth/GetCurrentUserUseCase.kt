package com.technote.aquasense.domain.usecase.auth

import com.technote.aquasense.data.model.User
import com.technote.aquasense.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Use Case para obter dados do usuário atual
 */
class GetCurrentUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    /**
     * Obtém o userId do usuário logado
     */
    suspend fun getUserId(): String? {
        return authRepository.getUserId()
    }
    
    /**
     * Obtém o perfil completo do usuário logado
     */
    suspend fun getUserProfile(): Result<User> {
        val userId = authRepository.getUserId()
        
        return if (userId.isNullOrBlank()) {
            Result.failure(Exception("Usuário não autenticado"))
        } else {
            authRepository.getUserProfile(userId)
        }
    }
    
    /**
     * Verifica se há um usuário logado
     */
    suspend fun isUserLoggedIn(): Boolean {
        val token = authRepository.getAuthToken()
        val userId = authRepository.getUserId()
        
        println("🔍 GetCurrentUserUseCase.isUserLoggedIn()")
        println("   Token: ${if (token.isNullOrBlank()) "VAZIO/NULL" else "Presente (${token.take(20)}...)"}")
        println("   UserId: ${if (userId.isNullOrBlank()) "VAZIO/NULL" else userId}")
        
        val isLoggedIn = !token.isNullOrBlank() && !userId.isNullOrBlank()
        println("   Resultado: $isLoggedIn")
        
        return isLoggedIn
    }
}
