package com.technote.aquasense.domain.usecase.fish

import com.technote.aquasense.data.model.Fish
import com.technote.aquasense.domain.repository.FishRepository
import javax.inject.Inject

class UpdateFishUseCase @Inject constructor(
    private val fishRepository: FishRepository
) {
    suspend operator fun invoke(
        fishId: String,
        nome: String,
        idade: Int,
        especie: String,
        peso: Float,
        quantidade: Int
    ): Result<Fish> {
        if (fishId.isBlank()) {
            return Result.failure(Exception("ID do peixe � obrigat�rio"))
        }
        if (nome.isBlank()) {
            return Result.failure(Exception("Nome do peixe � obrigat�rio"))
        }
        if (nome.trim().length > 15) {
            return Result.failure(Exception("Nome muito longo (m�ximo 15 caracteres)"))
        }
        if (idade < 0) {
            return Result.failure(Exception("Idade deve ser maior ou igual a 0"))
        }
        if (especie.isBlank()) {
            return Result.failure(Exception("Esp�cie do peixe � obrigat�ria"))
        }
        if (peso <= 0) {
            return Result.failure(Exception("Peso deve ser maior que 0"))
        }
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
            Result.failure(Exception("Erro ao atualizar peixe: ${e.message}"))
        }
    }
}
