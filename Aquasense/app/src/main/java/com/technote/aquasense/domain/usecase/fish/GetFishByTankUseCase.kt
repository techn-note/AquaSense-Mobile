package com.technote.aquasense.domain.usecase.fish

import com.technote.aquasense.data.model.Fish
import com.technote.aquasense.domain.repository.FishRepository
import javax.inject.Inject

/**
 * Use Case para obter peixes de um aquário
 *
 * Responsabilidades:
 * - Validar ID do aquário
 * - Buscar peixes no repository
 * - Ordenar por nome
 */
class GetFishByTankUseCase @Inject constructor(
    private val fishRepository: FishRepository
) {

    /**
     * Obtém todos os peixes de um aquário
     *
     * @param tankId ID do aquário
     * @return Result com lista de peixes ou erro
     */
    suspend operator fun invoke(tankId: String): Result<List<Fish>> {

        // Validação de entrada
        if (tankId.isBlank()) {
            return Result.failure(Exception("ID do aquário é obrigatório"))
        }

        return try {
            val result = fishRepository.getFishByTank(tankId)

            // Ordenar por nome
            result.map { fishList ->
                fishList.sortedBy { fish ->
                    fish.name.lowercase()
                }
            }
        } catch (e: Exception) {
            Result.failure(Exception("Erro ao buscar peixes: ${e.message}"))
        }
    }
}