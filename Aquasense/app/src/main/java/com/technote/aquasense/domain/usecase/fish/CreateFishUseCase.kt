package com.technote.aquasense.domain.usecase.fish

import com.technote.aquasense.data.model.Fish
import com.technote.aquasense.domain.repository.FishRepository
import javax.inject.Inject

class CreateFishUseCase @Inject constructor(
    private val fishRepository: FishRepository
) {
    suspend operator fun invoke(
        nome: String,
        idade: Int,
        especie: String,
        peso: Float,
        quantidade: Int,
        userId: String
    ): Result<Fish> {
        if (nome.isBlank()) {
            return Result.failure(Exception("Nome do peixe é obrigatório"))
        }
        if (nome.trim().length > 15) {
            return Result.failure(Exception("Nome muito longo (máximo 15 caracteres)"))
        }
        if (idade < 0) {
            return Result.failure(Exception("Idade deve ser maior ou igual a 0"))
        }
        if (especie.isBlank()) {
            return Result.failure(Exception("Espécie do peixe é obrigatória"))
        }
        if (peso <= 0) {
            return Result.failure(Exception("Peso deve ser maior que 0"))
        }
        if (quantidade < 1) {
            return Result.failure(Exception("Quantidade deve ser maior ou igual a 1"))
        }
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
            Result.failure(Exception("Erro ao criar peixe: ${e.message}"))
        }
    }
}
