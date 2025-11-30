package com.technote.aquasense.domain.usecase.tank

import com.technote.aquasense.data.model.Tank
import com.technote.aquasense.domain.repository.TankRepository
import javax.inject.Inject

/**
 * Use Case para criar novo aquário
 *
 * Responsabilidades:
 * - Validar dados de entrada
 * - Verificar regras de negócio
 * - Criar aquário via repository
 */
class CreateTankUseCase @Inject constructor(
    private val tankRepository: TankRepository
) {

    /**
     * Cria um novo aquário
     *
     * @param name Nome do aquário
     * @param volume Volume em litros
     * @param number Número do aquário (identificador único)
     * @param userId ID do proprietário
     * @return Result com aquário criado ou erro
     */
    suspend operator fun invoke(name: String, volume: Double, number: Double, userId: String): Result<Tank> {

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

        if (volume > 10000) { // 10.000 litros = limite razoável
            return Result.failure(Exception("Volume muito grande (máximo 10.000 litros)"))
        }

        // Validação do número
        if (number <= 0) {
            return Result.failure(Exception("Número do aquário deve ser maior que zero"))
        }

        // Validação do userId
        if (userId.isBlank()) {
            return Result.failure(Exception("ID do usuário é obrigatório"))
        }

        return try {
            // Verificar se já existe aquário com mesmo nome (opcional)
            val existingTank = tankRepository.getTankByName(name.trim())
            if (existingTank.isSuccess) {
                return Result.failure(Exception("Já existe um aquário com este nome"))
            }

            // Criar aquário
            tankRepository.createTank(
                name = name.trim(),
                volume = volume,
                number = number,
                userId = userId
            )
        } catch (e: Exception) {
            Result.failure(Exception("Não foi possível criar o tanque. Tente novamente."))
        }
    }
}