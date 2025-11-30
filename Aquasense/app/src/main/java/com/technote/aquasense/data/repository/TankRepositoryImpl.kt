package com.technote.aquasense.data.repository

import com.technote.aquasense.data.model.Tank
import com.technote.aquasense.data.remote.api.AquasenseApi
import com.technote.aquasense.data.remote.dto.CreateTankRequest
import com.technote.aquasense.data.remote.dto.UpdateTankRequest
import com.technote.aquasense.domain.repository.TankRepository
import com.technote.aquasense.utils.Constants
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TankRepositoryImpl @Inject constructor(
    private val api: AquasenseApi
) : TankRepository {

    override suspend fun getTanksByUser(userId: String): Result<List<Tank>> {
        var lastException: Exception? = null
        
        // Tentar até 3 vezes no nível do Repository
        repeat(3) { attempt ->
            try {
                println("🌐 Tentativa ${attempt + 1} - Fazendo requisição para: ${Constants.BASE_URL}tanque")
                val response = api.getTanks()

                println("🌐 Status da resposta: ${response.code()}")
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    println("🌐 Resposta recebida: $apiResponse")
                    
                    val tankDtos = apiResponse?.data ?: emptyList()
                    println("🌐 DTOs de tanques: ${tankDtos.size} tanques encontrados")
                    
                    // Log dos userIds dos tanques para debug
                    tankDtos.forEach { dto ->
                        println("🌐 Tanque: ${dto.name} - userId: ${dto.userId}")
                    }
                    
                    val tanks = tankDtos.map { dto ->
                        // CORREÇÃO: Sempre usar o ID real da API, nunca o nome como ID
                        val tankId = dto.id ?: ""
                        
                        // DEBUG: Imprimir informações detalhadas do DTO
                        println("🔍 DEBUG DTO - Nome: ${dto.name}, ID original: '${dto.id}', ID final: '$tankId'")
                        
                        val tank = Tank(
                            id = tankId,
                            name = dto.name ?: "",
                            volume = dto.volume ?: 0.0,
                            number = dto.number ?: 1.0,
                            userId = dto.userId ?: "",
                            createdAt = dto.createdAt
                        )
                        println("🔄 Convertendo DTO: ${dto.name} -> Tank: ${tank.name} (ID: '${tank.id}')")
                        tank
                    }

                    println("✅ Retornando ${tanks.size} tanques convertidos")
                    return Result.success(tanks)
                } else {
                    val errorBody = response.errorBody()?.string()
                    println("❌ Erro na resposta da API: ${response.code()} - ${response.message()}")
                    println("❌ Corpo do erro: $errorBody")
                    lastException = Exception("Não foi possível carregar a lista de tanques. Tente novamente.")
                }
            } catch (e: Exception) {
                println("❌ Exceção na tentativa ${attempt + 1}: ${e.message}")
                lastException = e
                
                // Aguardar antes da próxima tentativa (exceto na última)
                if (attempt < 2) {
                    try {
                        kotlinx.coroutines.delay(500L * (attempt + 1))
                    } catch (ignored: Exception) {}
                }
            }
        }
        
        println("❌ Todas as tentativas falharam")
        lastException?.printStackTrace()
        return Result.failure(lastException ?: Exception("Erro desconhecido ao buscar tanques"))
    }

    override suspend fun createTank(name: String, volume: Double, number: Double, userId: String): Result<Tank> {
        return try {
            val request = CreateTankRequest(
                name = name,
                capacity = volume,
                number = number,
                userId = userId
            )
            val response = api.createTank(request)

            if (response.isSuccessful) {
                val apiResponse = response.body()!!
                val dto = apiResponse.data
                val tank = Tank(
                    id = dto?.id ?: "",
                    name = dto?.name ?: "",
                    volume = dto?.volume ?: 0.0,
                    number = dto?.number ?: 1.0,
                    userId = dto?.userId ?: "",
                    createdAt = dto?.createdAt ?: ""
                )

                Result.success(tank)
            } else {
                Result.failure(Exception("Não foi possível criar o tanque. Verifique os dados e tente novamente."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateTank(tankId: String, name: String, volume: Double, number: Double): Result<Tank> {
        return try {
            println("🔄 Atualizando tanque - ID: $tankId, Nome: $name")
            
            val request = UpdateTankRequest(
                name = name,
                capacity = volume,
                number = number
            )
            val response = api.updateTank(tankId, request)

            if (response.isSuccessful) {
                val apiResponse = response.body()!!
                val dto = apiResponse.data
                val tank = Tank(
                    id = dto?.id ?: "",
                    name = dto?.name ?: "",
                    volume = dto?.volume ?: 0.0,
                    number = dto?.number ?: 1.0,
                    userId = dto?.userId ?: "",
                    createdAt = dto?.createdAt ?: ""
                )

                println("✅ Tanque atualizado com sucesso - ID: ${tank.id}, Nome: ${tank.name}")
                Result.success(tank)
            } else {
                println("❌ Erro ao atualizar tanque: ${response.code()} - ${response.message()}")
                Result.failure(Exception("Não foi possível atualizar os dados do tanque. Tente novamente."))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTankByName(tankName: String): Result<Tank> {
        return try {
            println("🔍 Buscando tanque pelo nome: $tankName")
            
            // Usar o endpoint específico por nome para obter o ID correto
            val response = api.getTankByName(tankName)

            if (response.isSuccessful) {
                val apiResponse = response.body()!!
                val dto = apiResponse.data
                    ?: return Result.failure(Exception("Tank not found with name: $tankName"))

                val tank = Tank(
                    id = dto.id ?: "",
                    name = dto.name ?: "",
                    volume = dto.volume ?: 0.0,
                    number = dto.number ?: 1.0,
                    userId = dto.userId ?: "",
                    createdAt = dto.createdAt ?: ""
                )

                println("✅ Tanque encontrado - Nome: ${tank.name}, ID Real: ${tank.id}")
                Result.success(tank)
            } else {
                println("❌ Erro ao buscar tanque por nome: ${response.code()} - ${response.message()}")
                Result.failure(Exception("Não foi possível encontrar o tanque solicitado."))
            }
        } catch (e: Exception) {
            println("❌ Exceção ao buscar tanque por nome: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun deleteTank(tankId: String): Result<Unit> {
        return try {
            println("🗑️ Deletando tanque - ID: $tankId")
            val response = api.deleteTank(tankId)

            if (response.isSuccessful) {
                println("✅ Tanque deletado com sucesso")
                Result.success(Unit)
            } else {
                println("❌ Erro ao deletar tanque: ${response.code()} - ${response.message()}")
                Result.failure(Exception("Não foi possível remover o tanque. Tente novamente."))
            }
        } catch (e: Exception) {
            println("❌ Exceção ao deletar tanque: ${e.message}")
            Result.failure(e)
        }
    }
}