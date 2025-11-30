package com.technote.aquasense.data.repository

import com.technote.aquasense.data.model.Fish
import com.technote.aquasense.data.remote.api.AquasenseApi
import com.technote.aquasense.data.remote.dto.CreatePeixeRequest
import com.technote.aquasense.data.remote.dto.UpdatePeixeRequest
import com.technote.aquasense.domain.repository.FishRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FishRepositoryImpl @Inject constructor(
    private val api: AquasenseApi
) : FishRepository {
    override suspend fun getFishByTank(tankId: String): Result<List<Fish>> {
        return try {
            val response = api.getPeixes()

            if (response.isSuccessful) {
                val apiResponse = response.body()!!
                val peixeDtos = apiResponse.data ?: emptyList()
                // Filtrar pelos peixes do tanque específico
                val fishList = peixeDtos.filter { it.tanque == tankId }.map { dto ->
                    Fish(
                        id = dto.id ?: "",
                        name = dto.nome,
                        idade = dto.idade,
                        species = dto.especie,
                        peso = dto.peso,
                        quantidade = dto.quantidade,
                        tankId = dto.tanque,
                        userId = dto.userId,
                        createdAt = dto.createdAt ?: ""
                    )
                }

                Result.success(fishList)
            } else {
                Result.failure(Exception("Não conseguimos buscar os peixes: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createFish(
        nome: String,
        idade: Int,
        especie: String,
        peso: Float,
        quantidade: Int,
        userId: String
    ): Result<Fish> {
        return try {
            val request = CreatePeixeRequest(
                nome = nome,
                idade = idade,
                especie = especie,
                peso = peso,
                quantidade = quantidade,
                userId = userId
            )
            val response = api.createPeixe(request)

            if (response.isSuccessful) {
                val apiResponse = response.body()!!
                val dto = apiResponse.data
                val fish = Fish(
                    id = dto?.id ?: "",
                    name = dto?.nome ?: "",
                    idade = dto?.idade ?: 0,
                    species = dto?.especie ?: "",
                    peso = dto?.peso ?: 0f,
                    quantidade = dto?.quantidade ?: 0,
                    tankId = dto?.tanque ?: "",
                    userId = dto?.userId ?: "",
                    createdAt = dto?.createdAt ?: ""
                )

                Result.success(fish)
            } else {
                Result.failure(Exception("Erro ao criar peixe: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateFish(
        fishId: String,
        nome: String,
        idade: Int,
        especie: String,
        peso: Float,
        quantidade: Int
    ): Result<Fish> {
        return try {
            val request = UpdatePeixeRequest(
                nome = nome,
                idade = idade,
                especie = especie,
                peso = peso,
                quantidade = quantidade
            )
            val response = api.updatePeixe(fishId, request)

            if (response.isSuccessful) {
                val apiResponse = response.body()!!
                val dto = apiResponse.data
                val fish = Fish(
                    id = dto?.id ?: "",
                    name = dto?.nome ?: "",
                    idade = dto?.idade ?: 0,
                    species = dto?.especie ?: "",
                    peso = dto?.peso ?: 0f,
                    quantidade = dto?.quantidade ?: 0,
                    tankId = dto?.tanque ?: "",
                    userId = dto?.userId ?: "",
                    createdAt = dto?.createdAt ?: ""
                )

                Result.success(fish)
            } else {
                Result.failure(Exception("Erro ao atualizar peixe: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getFishesByUser(userId: String): Result<List<Fish>> {
        return try {
            val response = api.getPeixes()

            if (response.isSuccessful) {
                val apiResponse = response.body()!!
                val peixeDtos = apiResponse.data ?: emptyList()
                // Filtrar pelos peixes do usuário específico
                val fishList = peixeDtos.filter { it.userId == userId }.map { dto ->
                    Fish(
                        id = dto.id ?: "",
                        name = dto.nome,
                        idade = dto.idade,
                        species = dto.especie,
                        peso = dto.peso,
                        quantidade = dto.quantidade,
                        tankId = dto.tanque,
                        userId = dto.userId,
                        createdAt = dto.createdAt ?: ""
                    )
                }

                Result.success(fishList)
            } else {
                Result.failure(Exception("Erro ao buscar peixes do usuário: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
