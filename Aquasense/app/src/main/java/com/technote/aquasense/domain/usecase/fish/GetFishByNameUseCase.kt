package com.technote.aquasense.domain.usecase.fish

import com.technote.aquasense.data.model.Fish
import com.technote.aquasense.domain.repository.FishRepository
import javax.inject.Inject

/**
 * Use Case para buscar peixe por nome
 *
 * Responsabilidades:
 * - Validar nome de entrada
 * - Buscar via repository
 * - Tratar caso não encontrado
 */
class GetFishByNameUseCase @Inject constructor(
    private val fishRepository: FishRepository
) {

    /**
     * Busca peixe por nome
     *
     * @param fishName Nome do peixe
     * @return Result com peixe encontrado ou erro
     */
    suspend operator fun invoke(fishName: String): Result<Fish> {

        // Validação de entrada
        if (fishName.isBlank()) {
            return Result.failure(Exception("Nome do peixe é obrigatório"))
        }

        return try {
            fishRepository.getFishByName(fishName.trim())
        } catch (e: Exception) {
            Result.failure(Exception("Erro ao buscar peixe: ${e.message}"))
        }
    }
}