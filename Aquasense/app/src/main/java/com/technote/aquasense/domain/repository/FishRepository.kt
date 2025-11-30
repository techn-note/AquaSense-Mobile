package com.technote.aquasense.domain.repository

import com.technote.aquasense.data.model.Fish

interface FishRepository {
    /**
     * Lista todos os peixes de um aquário
     * @param tankId ID do aquário
     * @return Result com lista de peixes ou erro
     */
    suspend fun getFishByTank(tankId: String): Result<List<Fish>>

    /**
     * Lista todos os peixes de um usuário
     * @param userId ID do usuário
     * @return Result com lista de peixes ou erro
     */
    suspend fun getFishesByUser(userId: String): Result<List<Fish>>

    /**
     * Adiciona um novo peixe
     * @param nome Nome do peixe
     * @param idade Idade do peixe
     * @param especie Espécie do peixe
     * @param peso Peso do peixe
     * @param quantidade Quantidade
     * @param userId ID do usuário
     * @return Result com peixe criado ou erro
     */
    suspend fun createFish(
        nome: String,
        idade: Int,
        especie: String,
        peso: Float,
        quantidade: Int,
        userId: String
    ): Result<Fish>

    /**
     * Atualiza um peixe existente
     * @param fishId ID do peixe
     * @param nome Novo nome
     * @param idade Nova idade
     * @param especie Nova espécie
     * @param peso Novo peso
     * @param quantidade Nova quantidade
     * @return Result com peixe atualizado ou erro
     */
    suspend fun updateFish(
        fishId: String,
        nome: String,
        idade: Int,
        especie: String,
        peso: Float,
        quantidade: Int
    ): Result<Fish>

    /**
     * Deleta um peixe
     * @param fishId ID do peixe
     * @return Result indicando sucesso ou erro
     */
    suspend fun deleteFish(fishId: String): Result<Unit>

    /**
     * Busca peixe por nome
     * @param fishName Nome do peixe
     * @return Result com peixe encontrado ou erro
     */
    suspend fun getFishByName(fishName: String): Result<Fish>
}