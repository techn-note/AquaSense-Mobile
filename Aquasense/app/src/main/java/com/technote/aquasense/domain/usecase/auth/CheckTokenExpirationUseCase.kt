package com.technote.aquasense.domain.usecase.auth

import com.technote.aquasense.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Use case para verificar expiração do token e tratar logout automático
 */
class CheckTokenExpirationUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    /**
     * Verifica se o token está expirado e executa logout se necessário
     * @return true se o token expirou e logout foi executado
     */
    suspend fun checkAndHandleExpiration(): Boolean {
        // Verifica se o token foi marcado como expirado pelo interceptor
        if (authRepository.isTokenMarkedExpired()) {
            println("⚠️ Token marcado como expirado - executando logout automático")
            executeLogout()
            return true
        }
        
        // Verifica se o token JWT está expirado
        if (authRepository.isTokenExpired()) {
            println("⚠️ Token JWT expirado - executando logout automático")
            executeLogout()
            return true
        }
        
        return false
    }
    
    /**
     * Executa o logout limpo
     */
    private suspend fun executeLogout() {
        try {
            authRepository.clearAuthToken()
            println("✅ Logout automático executado com sucesso")
        } catch (e: Exception) {
            println("❌ Erro durante logout automático: ${e.message}")
        }
    }
}