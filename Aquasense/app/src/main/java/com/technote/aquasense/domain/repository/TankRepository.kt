package com.technote.aquasense.domain.repository

import com.technote.aquasense.data.model.Tank

interface TankRepository {
    /**
     * Lista todos os aquários de um usuário
     * @param userId ID do usuário
     * @return Result com lista de aquários ou erro
     */
    suspend fun getTanksByUser(userId: String): Result<List<Tank>>

    /**
     * Cria um novo aquário
     * @param name Nome do aquário
     * @param volume Volume do aquário
     * @param number Número do aquário
     * @param userId ID do proprietário
     * @return Result com aquário criado ou erro
     */
    suspend fun createTank(name: String, volume: Double, number: Double, userId: String): Result<Tank>

    /**
     * Atualiza um aquário existente
     * @param tankId ID do aquário
     * @param name Novo nome
     * @param volume Novo volume
     * @param number Novo número
     * @return Result com aquário atualizado ou erro
     */
    suspend fun updateTank(tankId: String, name: String, volume: Double, number: Double): Result<Tank>

    /**
     * Deleta um aquário
     * @param tankId ID do aquário
     * @return Result indicando sucesso ou erro
     */
    suspend fun deleteTank(tankId: String): Result<Unit>

    /**
     * Busca aquário por nome
     * @param tankName Nome do aquário
     * @return Result com aquário encontrado ou erro
     */
    suspend fun getTankByName(tankName: String): Result<Tank>
}