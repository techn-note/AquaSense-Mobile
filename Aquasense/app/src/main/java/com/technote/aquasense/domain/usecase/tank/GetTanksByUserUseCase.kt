package com.technote.aquasense.domain.usecase.tank

import com.technote.aquasense.data.model.Tank
import com.technote.aquasense.domain.repository.TankRepository
import javax.inject.Inject

/**
 * Use Case para obter aquários de um usuário
 *
 * Responsabilidades:
 * - Validar ID do usuário
 * - Buscar aquários no repository
 * - Ordenar por data de criação
 */
class GetTanksByUserUseCase @Inject constructor(
    private val tankRepository: TankRepository
) {

    /**
     * Obtém todos os aquários de um usuário
     *
     * @param userId ID do usuário
     * @return Result com lista de aquários ou erro
     */
    suspend operator fun invoke(userId: String): Result<List<Tank>> {

        // Validação de entrada
        if (userId.isBlank()) {
            return Result.failure(Exception("ID do usuário é obrigatório"))
        }

        return try {
            val result = tankRepository.getTanksByUser(userId)

            // Ordenar por data de criação (mais recentes primeiro)
            result.map { tanks ->
                tanks.sortedByDescending { tank ->
                    tank.createdAt ?: ""
                }
            }
        } catch (e: Exception) {
            Result.failure(Exception("Não foi possível carregar a lista de tanques."))
        }
    }
}