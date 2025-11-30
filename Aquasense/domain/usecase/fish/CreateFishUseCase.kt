package com.technote.aquasense.domain.usecase.fish

import com.technote.aquasense.data.model.Fish
import com.technote.aquasense.domain.repository.FishRepository
import javax.inject.Inject

/**
 * Use Case para criar novo peixe
 *
 * Responsabilidades:
 * - Validar dados de entrada
 * - Verificar regras de negócio
 * - Criar peixe via repository
 */
class CreateFishUseCase @Inject constructor(
    private val fishRepository: FishRepository
) {

    /**
     * Cria um novo peixe
     *
     * @param nome Nome do peixe
     * @param idade Idade do peixe
     * @param especie Espécie do peixe
     * @param peso Peso do peixe
     * @param quantidade Quantidade do peixe
     * @param userId ID do usuário
     * @return Result com peixe criado ou erro
     */
    suspend operator fun invoke(
        nome: String,
        idade: Int,
        especie: String,
        peso: Float,
        quantidade: Int,
        userId: String
    ): Result<Fish> {

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

        // Validação do userId
        if (userId.isBlank()) {
            return Result.failure(Exception("ID do usuário é obrigatório"))
        }

        return try {
            fishRepository.createFish(
                nome = nome.trim(),
                idade = idade,
                especie = especie.trim(),
                peso = peso,
                quantidade = quantidade,
                userId = userId
            )
        } catch (e: Exception) {
            Result.failure(Exception("Não foi possível adicionar o peixe. Tente novamente."))
        }
    }
}