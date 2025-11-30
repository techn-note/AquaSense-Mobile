package com.technote.aquasense.domain.usecase.auth

import com.technote.aquasense.data.model.User
import com.technote.aquasense.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Use Case para autenticação de usuário
 *
 * Responsabilidades:
 * - Validar email e senha
 * - Chamar repository de autenticação
 * - Salvar token automaticamente
 *
 * @param authRepository Repository de autenticação
 */
class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {

    /**
     * Executa login do usuário
     *
     * @param email Email do usuário
     * @param password Senha do usuário
     * @return Result com token e dados do usuário ou erro
     */
    suspend operator fun invoke(email: String, password: String): Result<Pair<String, User>> {

        // Validação de entrada
        if (email.isBlank()) {
            return Result.failure(Exception("Email é obrigatório"))
        }

        if (!isValidEmail(email)) {
            return Result.failure(Exception("Email inválido"))
        }

        if (password.isBlank()) {
            return Result.failure(Exception("Senha é obrigatória"))
        }

        if (password.length < 6) {
            return Result.failure(Exception("Senha deve ter pelo menos 6 caracteres"))
        }

        // Chama repository
        return try {
            val result = authRepository.login(email.trim().lowercase(), password)

            // Se login bem-sucedido, salva o token automaticamente
            result.onSuccess { (token, _) ->
                authRepository.saveAuthToken(token)
            }

            result
        } catch (e: Exception) {
            Result.failure(Exception("Erro no login: ${e.message}"))
        }
    }

    /**
     * Validação simples de email
     */
    private fun isValidEmail(email: String): Boolean {
        return email.contains("@") && email.contains(".")
    }
}