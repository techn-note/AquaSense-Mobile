package com.technote.aquasense.domain.usecase.fish

import com.technote.aquasense.data.model.Fish
import com.technote.aquasense.domain.repository.FishRepository
import javax.inject.Inject

/**
 * Use Case para buscar todos os peixes de um usuário
 *
 * Responsabilidades:
 * - Validar ID do usuário
 * - Buscar peixes via repository
 */
class GetFishesByUserUseCase @Inject constructor(
    private val fishRepository: FishRepository
) {

    /**
     * Busca todos os peixes de um usuário
     *
     * @param userId ID do usuário
     * @return Result com lista de peixes ou erro
     */
    suspend operator fun invoke(userId: String): Result<List<Fish>> {
        if (userId.isBlank()) {
            return Result.failure(Exception("ID do usuário é obrigatório"))
        }

        return try {
            fishRepository.getFishesByUser(userId)
        } catch (e: Exception) {
            Result.failure(Exception("Não foi possível carregar a lista de peixes."))
        }
    }
}
