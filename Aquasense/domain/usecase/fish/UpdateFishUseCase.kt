package com.technote.aquasense.domain.usecase.fish

import com.technote.aquasense.data.model.Fish
import com.technote.aquasense.domain.repository.FishRepository
import javax.inject.Inject

/**
 * Use Case para atualizar peixe existente
 *
 * Responsabilidades:
 * - Validar dados de entrada
 * - Verificar se peixe existe
 * - Atualizar via repository
 */
class UpdateFishUseCase @Inject constructor(
    private val fishRepository: FishRepository
) {

    /**
     * Atualiza um peixe existente
     *
     * @param fishId ID do peixe
     * @param nome Novo nome
     * @param idade Nova idade
     * @param especie Nova espécie
     * @param peso Novo peso
     * @param quantidade Nova quantidade
     * @return Result com peixe atualizado ou erro
     */
    suspend operator fun invoke(
        fishId: String,
        nome: String,
        idade: Int,
        especie: String,
        peso: Float,
        quantidade: Int
    ): Result<Fish> {

        // Validação do fishId
        if (fishId.isBlank()) {
            return Result.failure(Exception("ID do peixe é obrigatório"))
        }

        // Validação do nome
        if (nome.isBlank()) {
            return Result.failure(Exception("Nome do peixe é obrigatório"))
        }

        if (nome.trim().length > 15) {
            return Result.failure(Exception("Nome muito longo (máximo 15 caracteres)"))
        }

        // Validação da idade
        if (idade < 0) {
            return Result.failure(Exception("Idade deve ser maior ou igual a 0"))
        }

        // Validação da espécie
        if (especie.isBlank()) {
            return Result.failure(Exception("Espécie do peixe é obrigatória"))
        }

        // Validação do peso
        if (peso <= 0) {
            return Result.failure(Exception("Peso deve ser maior que 0"))
        }

        // Validação da quantidade
        if (quantidade < 1) {
            return Result.failure(Exception("Quantidade deve ser maior ou igual a 1"))
        }

        return try {
            fishRepository.updateFish(
                fishId = fishId,
                nome = nome.trim(),
                idade = idade,
                especie = especie.trim(),
                peso = peso,
                quantidade = quantidade
            )
        } catch (e: Exception) {
            Result.failure(Exception("Não foi possível atualizar o peixe. Tente novamente."))
        }
    }
}