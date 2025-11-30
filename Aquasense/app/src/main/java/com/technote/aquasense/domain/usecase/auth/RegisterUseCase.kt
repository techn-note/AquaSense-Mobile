package com.technote.aquasense.domain.usecase.auth

import com.technote.aquasense.data.model.User
import com.technote.aquasense.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Use Case para registro de novo usuário
 *
 * Responsabilidades:
 * - Validar dados de entrada
 * - Verificar força da senha
 * - Chamar repository de registro
 *
 * @param authRepository Repository de autenticação
 */
class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {

    /**
     * Executa registro de novo usuário
     *
     * @param name Nome do usuário
     * @param email Email do usuário
     * @param password Senha do usuário
     * @return Result com dados do usuário criado ou erro
     */
    suspend operator fun invoke(
        name: String,
        email: String,
        password: String,
        confirmPassword: String,
        age: Int,
        country: String
    ): Result<Pair<String, User>> {

        // Validação do nome
        if (name.isBlank()) {
            return Result.failure(Exception("Nome é obrigatório"))
        }

        if (name.trim().length < 2) {
            return Result.failure(Exception("Nome deve ter pelo menos 2 caracteres"))
        }

        // Validação do email
        if (email.isBlank()) {
            return Result.failure(Exception("Email é obrigatório"))
        }

        if (!isValidEmail(email)) {
            return Result.failure(Exception("Email inválido"))
        }

        // Validação da senha
        if (password.isBlank()) {
            return Result.failure(Exception("Senha é obrigatória"))
        }

        // Validação de confirmação de senha
        if (confirmPassword.isBlank()) {
            return Result.failure(Exception("Confirmação de senha é obrigatória"))
        }

        if (password != confirmPassword) {
            return Result.failure(Exception("As senhas não coincidem"))
        }



        val passwordValidation = validatePassword(password)
        if (!passwordValidation.first) {
            return Result.failure(Exception(passwordValidation.second))
        }

        // Registrar usuário
        return try {
            val registerResult = authRepository.registerWithDetails(
                name = name.trim(),
                email = email.trim().lowercase(),
                password = password,
                age = age,
                country = country.trim()
            )
            
            // Se registro foi bem-sucedido, fazer login automático
            registerResult.fold(
                onSuccess = { user ->
                    // Fazer login automático após registro
                    val loginResult = authRepository.login(
                        email = email.trim().lowercase(),
                        password = password
                    )
                    
                    loginResult.fold(
                        onSuccess = { (token, loggedUser) ->
                            Result.success(Pair(token, loggedUser))
                        },
                        onFailure = { loginError ->
                            // Registro OK, mas login falhou - retornar erro de login
                            Result.failure(Exception("Usuário registrado, mas erro no login automático: ${loginError.message}"))
                        }
                    )
                },
                onFailure = { registerError ->
                    Result.failure(registerError)
                }
            )
        } catch (e: Exception) {
            Result.failure(Exception("Erro no registro: ${e.message}"))
        }
    }

    /**
     * Validação simples de email
     */
    private fun isValidEmail(email: String): Boolean {
        return email.contains("@") && email.contains(".")
    }

    /**
     * Validação de força da senha
     * @return Pair<isValid, errorMessage>
     */
    private fun validatePassword(password: String): Pair<Boolean, String> {
        if (password.length < 6) {
            return false to "Senha deve ter pelo menos 6 caracteres"
        }

        if (password.length > 50) {
            return false to "Senha muito longa (máximo 50 caracteres)"
        }

        // Opcional: verificar se tem ao menos uma letra e um número
        val hasLetter = password.any { it.isLetter() }
        val hasDigit = password.any { it.isDigit() }

        if (!hasLetter || !hasDigit) {
            return false to "Senha deve conter pelo menos uma letra e um número"
        }

        return true to "Senha válida"
    }
}