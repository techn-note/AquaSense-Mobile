package com.technote.aquasense.domain.usecase.tank

import com.technote.aquasense.data.model.Tank
import com.technote.aquasense.domain.repository.TankRepository
import javax.inject.Inject

/**
 * Use Case para buscar aquário por nome
 *
 * Responsabilidades:
 * - Validar nome de entrada
 * - Buscar via repository
 * - Tratar caso não encontrado
 */
class GetTankByNameUseCase @Inject constructor(
    private val tankRepository: TankRepository
) {

    /**
     * Busca aquário por nome
     *
     * @param tankName Nome do aquário
     * @return Result com aquário encontrado ou erro
     */
    suspend operator fun invoke(tankName: String): Result<Tank> {

        // Validação de entrada
        if (tankName.isBlank()) {
            return Result.failure(Exception("Nome do aquário é obrigatório"))
        }

        return try {
            tankRepository.getTankByName(tankName.trim())
        } catch (e: Exception) {
            Result.failure(Exception("Não foi possível encontrar o tanque solicitado."))
        }
    }
}