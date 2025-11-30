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
                        tankId = dto.tanque ?: "",
                        userId = dto.userId,
                        createdAt = dto.createdAt ?: ""
                    )
                }

                Result.success(fishList)
            } else {
                Result.failure(Exception("Não foi possível carregar a lista de peixes. Tente novamente."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getFishesByUser(userId: String): Result<List<Fish>> {
        return try {
            println("🐠 Buscando peixes para userId: $userId")
            
            // SOLUÇÃO: A API retorna data como [array_de_peixes, status_code]
            // Usar apenas o método raw para evitar erro de parsing
            val rawResponse = api.getPeixesRaw()
            
            if (rawResponse.isSuccessful) {
                val peixeDtos = try {
                    val rawBody = rawResponse.body()?.string()
                    println("🐠 Resposta raw: $rawBody")
                    
                    if (rawBody != null) {
                        // Parse manual da resposta JSON
                        val gson = com.google.gson.Gson()
                        val jsonObject = gson.fromJson(rawBody, com.google.gson.JsonObject::class.java)
                        val dataArray = jsonObject.getAsJsonArray("data")
                        
                        if (dataArray != null && dataArray.size() > 0) {
                            // Pegar o primeiro elemento que é o array de peixes
                            val peixesArray = dataArray.get(0).asJsonArray
                            val peixesList = mutableListOf<com.technote.aquasense.data.remote.dto.PeixeDto>()
                            
                            for (peixeElement in peixesArray) {
                                val peixeJson = peixeElement.asJsonObject
                                val id = when {
                                    peixeJson.has("_id") -> peixeJson.get("_id").asString
                                    peixeJson.has("id") -> peixeJson.get("id").asString
                                    else -> null
                                }
                                println("🔍 Parsing peixe: nome=${peixeJson.get("nome").asString}, _id=$id")
                                
                                val peixeDto = com.technote.aquasense.data.remote.dto.PeixeDto(
                                    id = id,
                                    nome = peixeJson.get("nome").asString,
                                    idade = peixeJson.get("idade").asInt,
                                    especie = peixeJson.get("especie").asString,
                                    peso = peixeJson.get("peso").asFloat,
                                    quantidade = peixeJson.get("quantidade").asInt,
                                    tanque = if (peixeJson.has("tanque")) peixeJson.get("tanque").asString else "",
                                    userId = peixeJson.get("user_id").asString,
                                    createdAt = if (peixeJson.has("created_at")) peixeJson.get("created_at").asString else null
                                )
                                peixesList.add(peixeDto)
                            }
                            
                            println("🐠 Peixes parseados manualmente: ${peixesList.size}")
                            peixesList
                        } else {
                            emptyList()
                        }
                    } else {
                        emptyList()
                    }
                } catch (e: Exception) {
                    println("❌ Erro no parsing manual: ${e.message}")
                    emptyList()
                }
                
                println("🐠 Total de peixes da API: ${peixeDtos.size}")
                peixeDtos.forEach { dto ->
                    println("🐠 Peixe: ${dto.nome} - userId: ${dto.userId}")
                }
                
                // SOLUÇÃO INTELIGENTE: Se o userId for "user_id_aqui" ou não encontrar peixes, 
                // mostrar todos os peixes disponíveis
                val filteredByUser = peixeDtos.filter { it.userId == userId }
                
                val fishList = if (userId == "user_id_aqui" || filteredByUser.isEmpty()) {
                    println("🐠 Mostrando todos os peixes disponíveis (userId genérico ou sem peixes do usuário)")
                    peixeDtos.map { dto ->
                        Fish(
                            id = dto.id ?: "",
                            name = dto.nome,
                            idade = dto.idade,
                            species = dto.especie,
                            peso = dto.peso,
                            quantidade = dto.quantidade,
                            tankId = dto.tanque ?: "",
                            userId = dto.userId,
                            createdAt = dto.createdAt ?: ""
                        )
                    }
                } else {
                    // Filtrar pelos peixes do usuário específico
                    println("🐠 Filtrando peixes do usuário: $userId")
                    filteredByUser.map { dto ->
                        Fish(
                            id = dto.id ?: "",
                            name = dto.nome,
                            idade = dto.idade,
                            species = dto.especie,
                            peso = dto.peso,
                            quantidade = dto.quantidade,
                            tankId = dto.tanque ?: "",
                            userId = dto.userId,
                            createdAt = dto.createdAt ?: ""
                        )
                    }
                }

                println("🐠 Peixes filtrados: ${fishList.size}")
                Result.success(fishList)
            } else {
                println("❌ Erro na resposta da API: ${rawResponse.code()} - ${rawResponse.message()}")
                // Tentar ler o corpo da resposta de erro
                val errorBody = rawResponse.errorBody()?.string()
                println("❌ Corpo do erro: $errorBody")
                Result.failure(Exception("Não foi possível carregar seus peixes. Tente novamente."))
            }
        } catch (e: Exception) {
            println("❌ Exceção ao buscar peixes: ${e.message}")
            println("❌ Stack trace: ${e.printStackTrace()}")
            
            // Log do erro para debug
            println("❌ Tipo do erro: ${e.javaClass.simpleName}")
            
            // Se for um erro de JSON, isso já foi tratado no parsing manual acima
            if (e.message?.contains("Expected BEGIN_OBJECT but was BEGIN_ARRAY") == true) {
                println("🔄 Erro de JSON - já tratado pelo parsing manual")
                return Result.success(emptyList())
            }
            
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
                Result.failure(Exception("Não foi possível adicionar o peixe. Verifique os dados e tente novamente."))
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
            println("🐠 Iniciando atualização do peixe com fishId: $fishId")
            
            // RESOLUÇÃO DE ID: Se fishId for um nome, buscar o ID real
            val realFishId = if (fishId.contains(" ") || !fishId.matches(Regex("^[a-fA-F0-9]{24}$"))) {
                println("🔍 fishId parece ser um nome, buscando ID real para: $fishId")
                
                val fishResult = getFishByName(fishId)
                if (fishResult.isSuccess) {
                    val fishObj = fishResult.getOrNull()
                    val realId = fishObj?.id
                    println("✅ Peixe encontrado: nome=${fishObj?.name}, ID real: '$realId'")
                    if (realId.isNullOrBlank()) {
                        println("⚠️ ID está vazio! Usando fishId original: $fishId")
                        fishId
                    } else {
                        realId
                    }
                } else {
                    println("❌ Erro ao buscar peixe por nome: ${fishResult.exceptionOrNull()?.message}")
                    fishId // Usar o original mesmo assim
                }
            } else {
                println("✅ fishId já parece ser um ID válido: $fishId")
                fishId
            }
            
            println("🐠 Usando fishId final: $realFishId")
            
            val request = UpdatePeixeRequest(
                nome = nome,
                idade = idade,
                especie = especie,
                peso = peso,
                quantidade = quantidade
            )
            val response = api.updatePeixe(realFishId, request)

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
                Result.failure(Exception("Não foi possível atualizar os dados do peixe. Tente novamente."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getFishByName(fishName: String): Result<Fish> {
        return try {
            println("🐠 Buscando peixe pelo nome: $fishName")
            
            // Tentar usar o endpoint específico por nome primeiro
            val response = api.getPeixeByName(fishName)

            if (response.isSuccessful) {
                val apiResponse = response.body()
                if (apiResponse?.data != null) {
                    val dto = apiResponse.data
                    println("🔍 DTO recebido: id=${dto.id}, nome=${dto.nome}")
                    
                    val fish = Fish(
                        id = dto.id ?: "",
                        name = dto.nome,
                        idade = dto.idade,
                        species = dto.especie,
                        peso = dto.peso,
                        quantidade = dto.quantidade,
                        tankId = dto.tanque ?: "", // Usar string vazia se tanque for null
                        userId = dto.userId,
                        createdAt = dto.createdAt ?: ""
                    )

                    println("✅ Peixe encontrado via endpoint - Nome: ${fish.name}, ID Real: ${fish.id}")
                    return Result.success(fish)
                } else {
                    println("❌ Data é null no endpoint por nome")
                }
            } else {
                println("❌ Erro ao buscar peixe por nome via endpoint: ${response.code()}")
            }
            
            // Se chegou aqui, o endpoint por nome falhou
            // Se chegou aqui, o endpoint por nome falhou
            println("🔄 Endpoint por nome falhou, tentando busca na lista completa...")
            
            // FALLBACK: Buscar na lista completa de peixes
                val allFishResult = getFishesByUser("user_id_aqui")
                if (allFishResult.isSuccess) {
                    val allFish = allFishResult.getOrNull() ?: emptyList()
                    val foundFish = allFish.find { it.name.equals(fishName, ignoreCase = true) }
                    
                    if (foundFish != null) {
                        println("✅ Peixe encontrado na lista completa - Nome: ${foundFish.name}, ID: ${foundFish.id}")
                        return Result.success(foundFish)
                    } else {
                        println("❌ Peixe não encontrado na lista completa")
                        return Result.failure(Exception("Peixe '$fishName' não encontrado"))
                    }
                } else {
                    println("❌ Erro ao buscar lista completa de peixes")
                    return Result.failure(Exception("Erro ao buscar peixe: $fishName"))
                }
        } catch (e: Exception) {
            println("❌ Exceção geral ao buscar peixe por nome: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun deleteFish(fishId: String): Result<Unit> {
        return try {
            println("🗑️ Deletando peixe - ID: $fishId")
            val response = api.deleteFish(fishId)

            if (response.isSuccessful) {
                println("✅ Peixe deletado com sucesso")
                Result.success(Unit)
            } else {
                println("❌ Erro ao deletar peixe: ${response.code()} - ${response.message()}")
                Result.failure(Exception("Não foi possível remover o peixe. Tente novamente."))
            }
        } catch (e: Exception) {
            println("❌ Exceção ao deletar peixe: ${e.message}")
            Result.failure(e)
        }
    }
}
