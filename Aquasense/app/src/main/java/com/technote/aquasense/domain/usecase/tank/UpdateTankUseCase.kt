package com.technote.aquasense.domain.usecase.tank

import com.technote.aquasense.data.model.Tank
import com.technote.aquasense.domain.repository.TankRepository
import javax.inject.Inject

/**
 * Use Case para atualizar aquário existente
 *
 * Responsabilidades:
 * - Validar dados de entrada
 * - Verificar se aquário existe
 * - Atualizar via repository
 */
class UpdateTankUseCase @Inject constructor(
    private val tankRepository: TankRepository
) {

    /**
     * Atualiza um aquário existente
     *
     * @param tankId ID do aquário
     * @param name Novo nome
     * @param volume Novo volume
     * @param number Novo número
     * @return Result com aquário atualizado ou erro
     */
    suspend operator fun invoke(tankId: String, name: String, volume: Double, number: Double): Result<Tank> {

        // Validação do tankId
        if (tankId.isBlank()) {
            return Result.failure(Exception("ID do aquário é obrigatório"))
        }

        // Validação do nome
        if (name.isBlank()) {
            return Result.failure(Exception("Nome do aquário é obrigatório"))
        }

        if (name.trim().length < 2) {
            return Result.failure(Exception("Nome deve ter pelo menos 2 caracteres"))
        }

        if (name.length > 50) {
            return Result.failure(Exception("Nome muito longo (máximo 50 caracteres)"))
        }

        // Validação do volume
        if (volume <= 0) {
            return Result.failure(Exception("Volume deve ser maior que zero"))
        }

        if (volume > 10000) {
            return Result.failure(Exception("Volume muito grande (máximo 10.000 litros)"))
        }

        // Validação do número
        if (number <= 0) {
            return Result.failure(Exception("Número do aquário deve ser maior que zero"))
        }

        return try {
            tankRepository.updateTank(
                tankId = tankId,
                name = name.trim(),
                volume = volume,
                number = number
            )
        } catch (e: Exception) {
            Result.failure(Exception("Não foi possível atualizar o tanque. Tente novamente."))
        }
    }
}