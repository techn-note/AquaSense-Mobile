package com.technote.aquasense.domain.repository

import com.technote.aquasense.data.model.User

/**
 * Interface do Repository de Autenticação
 * Define o contrato para operações de autenticação
 */

interface AuthRepository {
    /**
     * Realiza login do usuário
     * @param email Email do usuário
     * @param password Senha do usuário
     * @return Result com token e dados do usuário ou erro
     */
    suspend fun login(email: String, password: String): Result<Pair<String, User>>

    /**
     * Realiza registro do usuário
     * @param name Nome do usuário
     * @param email Email do usuário
     * @param password Senha do usuário
     * @return Result com dados do usuário ou erro
     */
    suspend fun register(name: String, email: String, password: String): Result<User>

    /**
     * Realiza registro do usuário com idade e país
     * @param name Nome do usuário
     * @param email Email do usuário
     * @param password Senha do usuário
     * @param age Idade do usuário
     * @param country País do usuário
     * @return Result com dados do usuário ou erro
     */
    suspend fun registerWithDetails(name: String, email: String, password: String, age: Int, country: String): Result<User>

    /**
     * Obtém perfil do usuário
     * @param userId ID do usuário
     * @return Result com dados do usuário ou erro
     */
    suspend fun getUserProfile(userId: String): Result<User>

    /**
     * Atualiza perfil do usuário (nome, email, age, country)
     * @param name Nome do usuário (opcional)
     * @param email Email do usuário (opcional)
     * @param age Idade do usuário (opcional)
     * @param country País do usuário (opcional)
     * @return Result com dados atualizados do usuário ou erro
     */
    suspend fun updateUserProfile(name: String?, email: String?, age: Int?, country: String?): Result<User>

    /**
     * Atualiza apenas a senha do usuário
     * @param password Nova senha do usuário
     * @return Result com dados atualizados do usuário ou erro
     */
    suspend fun updateUserPassword(password: String): Result<User>

    /**
     * Salva token de autenticação
     * @param token Token JWT
     */
    suspend fun saveAuthToken(token: String)
    
    /**
     * Obtém token de autenticação salvo
     * @return Token JWT ou null se não existir
     */
    suspend fun getAuthToken(): String?
    
    /**
     * Salva ID do usuário logado
     * @param userId ID do usuário
     */
    suspend fun saveUserId(userId: String)
    
    /**
     * Obtém ID do usuário logado
     * @return ID do usuário ou null se não existir
     */
    suspend fun getUserId(): String?

    /**
     * Salva email do usuário logado
     * @param email Email do usuário
     */
    suspend fun saveUserEmail(email: String)
    
    /**
     * Obtém email do usuário logado
     * @return Email do usuário ou null se não existir
     */
    suspend fun getUserEmail(): String?

    /**
     * Remove token de autenticação (logout)
     */
    suspend fun clearAuthToken()
    
    /**
     * Verifica se o token atual está expirado
     * @return true se o token está expirado ou não existe
     */
    suspend fun isTokenExpired(): Boolean
    
    /**
     * Verifica se o token foi marcado como expirado pelo interceptor
     * @return true se o token foi marcado como expirado
     */
    suspend fun isTokenMarkedExpired(): Boolean
    
    /**
     * Limpa a marcação de token expirado
     */
    suspend fun clearTokenExpiredFlag()
}