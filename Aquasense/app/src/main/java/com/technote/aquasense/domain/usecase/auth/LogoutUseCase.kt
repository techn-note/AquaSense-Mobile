package com.technote.aquasense.domain.usecase.auth

import com.technote.aquasense.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Use Case para logout do usuário
 *
 * Responsabilidades:
 * - Limpar token de autenticação
 * - Limpar dados locais (futuro)
 *
 * @param authRepository Repository de autenticação
 */
class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {

    /**
     * Executa logout do usuário
     *
     * @return Result indicando sucesso ou erro
     */
    suspend operator fun invoke(): Result<Unit> {
        return try {
            // Limpa token de autenticação
            authRepository.clearAuthToken()

            // TODO: Limpar cache local quando implementar
            // cacheRepository.clearUserData()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Erro no logout: ${e.message}"))
        }
    }
}