package com.technote.aquasense.data.repository

import com.technote.aquasense.data.model.SensorData
import com.technote.aquasense.data.remote.api.AquasenseApi
import com.technote.aquasense.data.remote.dto.UpdateSensorDataRequest
import com.technote.aquasense.domain.repository.SensorRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SensorRepositoryImpl @Inject constructor(
    private val api: AquasenseApi
) : SensorRepository {

    override suspend fun getSensorData(tankId: String): Result<SensorData> {
        return try {
            // A API espera tipo e tanque como parâmetros
            // Vamos buscar todos os tipos de sensores
            val sensorTypes = listOf("Temperatura", "Ph", "Tds", "Volume")
            
            var temperature: Double? = null
            var ph: Double? = null
            var tds: Double? = null
            var volume: Double? = null
            var sensorId: String? = null
            var createdAt: String? = null
            
            // Fazer chamadas para cada tipo de sensor
            for (type in sensorTypes) {
                try {
                    val response = api.getLatestSensorData(tipo = type, tanqueId = tankId)
                    
                    if (response.isSuccessful) {
                        val apiResponse = response.body()
                        val sensorDto = apiResponse?.data
                        
                        if (sensorDto != null && sensorDto.value != null) {
                            createdAt = sensorDto.date
                            
                            // Mapear o valor para o campo correto baseado no tipo
                            when (type) {
                                "Temperatura" -> temperature = sensorDto.value
                                "Ph" -> ph = sensorDto.value
                                "Tds" -> tds = sensorDto.value
                                "Volume" -> volume = sensorDto.value
                            }
                        }
                    }
                } catch (e: Exception) {
                    // Continuar para o próximo tipo se houver erro
                }
            }

            // Retornar os dados combinados (usar 0.0 como padrão se não houver dados)
            val sensorData = SensorData(
                id = sensorId,
                tankId = tankId,
                temperature = temperature ?: 0.0,
                ph = ph ?: 0.0,
                tds = tds ?: 0.0,
                volume = volume ?: 0.0,
                createdAt = createdAt
            )
            
            println("📊 Dados finais do sensor: temp=$temperature, ph=$ph, tds=$tds, volume=$volume")
            Result.success(sensorData)
        } catch (e: Exception) {
            println("❌ Erro geral ao buscar dados dos sensores: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun getSensorHistory(tankId: String): Result<List<SensorData>> {
        return try {
            // A API espera tipo e tanque como parâmetros
            // Vamos buscar todos os tipos de sensores (ÚLTIMOS 10)
            val sensorTypes = listOf("Temperatura", "Ph", "Tds", "Volume")

            // Map date -> partial SensorData
            val mapByDate = mutableMapOf<String, SensorData>()

            for (type in sensorTypes) {
                try {
                    val response = api.getLatestSensorHistory(tipo = type, tanqueId = tankId)

                    if (response.isSuccessful) {
                        val apiResponse = response.body()
                        val list = apiResponse?.data ?: emptyList()

                        // API retorna os mais recentes primeiro — pegamos até 10
                        val limited = if (list.size > 10) list.take(10) else list

                        // Processar cada dto e criar entradas únicas (mesmo com datas iguais)
                        limited.forEachIndexed { index, dto ->
                            // Usar data + índice para evitar sobrescrita de registros com mesma data
                            val date = dto.date ?: ""
                            val uniqueKey = "$date-$index"
                            
                            val existing = mapByDate[uniqueKey]
                            if (existing == null) {
                                // Criar entry com só um campo preenchido
                                val sensorData = SensorData(
                                    id = null,
                                    tankId = tankId,
                                    temperature = if (type.equals("Temperatura", true)) (dto.value ?: 0.0) else 0.0,
                                    ph = if (type.equals("Ph", true)) (dto.value ?: 0.0) else 0.0,
                                    tds = if (type.equals("Tds", true)) (dto.value ?: 0.0) else 0.0,
                                    volume = if (type.equals("Volume", true)) (dto.value ?: 0.0) else 0.0,
                                    createdAt = date
                                )
                                mapByDate[uniqueKey] = sensorData
                            } else {
                                // Merge com entry existente
                                val updated = when (type) {
                                    "Temperatura" -> existing.copy(temperature = dto.value ?: 0.0)
                                    "Ph" -> existing.copy(ph = dto.value ?: 0.0)
                                    "Tds" -> existing.copy(tds = dto.value ?: 0.0)
                                    "Volume" -> existing.copy(volume = dto.value ?: 0.0)
                                    else -> existing
                                }
                                mapByDate[uniqueKey] = updated
                            }
                        }
                    }
                } catch (e: Exception) {
                    // Log e continue para próximo tipo
                    println("Erro ao buscar $type: ${e.message}")
                }
            }

            // Converter map para lista e ordenar por data
            val resultList = mapByDate.values.toList().sortedByDescending { it.createdAt ?: "" }

            Result.success(resultList)

        } catch (e: Exception) {
            Result.failure(Exception("Erro ao buscar histórico dos sensores: ${e.message}"))
        }
    }

    override suspend fun getAllSensorData(tankId: String): Result<List<SensorData>> {
        return try {
            println("📊 Buscando TODOS os dados dos sensores para relatório - tanque: $tankId")
            
            // Buscar TODOS os sensores do usuário usando o endpoint /sensores
            val response = api.getAllSensors()
            
            if (!response.isSuccessful) {
                return Result.failure(Exception("Não foi possível carregar os dados dos sensores."))
            }
            
            val apiResponse = response.body()
            val allSensors = apiResponse?.data ?: emptyList()
            
            println("📊 Total de sensores encontrados: ${allSensors.size}")
            
            // Filtrar apenas os sensores do tanque específico
            val tankSensors = allSensors.filter { sensor ->
                sensor.tank?.equals(tankId, ignoreCase = true) == true
            }
            
            println("📊 Sensores do tanque $tankId: ${tankSensors.size}")
            
            // Retornar sensores individuais como estão - será processado no ViewModel
            println("📊 Retornando ${tankSensors.size} sensores individuais para relatório")
            
            // Criar uma estrutura simples para cada sensor individual
            val sensorDataList = tankSensors.map { dto ->
                SensorData(
                    id = null,
                    tankId = dto.tank ?: tankId,
                    temperature = if (dto.type?.equals("Temperatura", true) == true) (dto.value ?: 0.0) else 0.0,
                    ph = if (dto.type?.equals("Ph", true) == true) (dto.value ?: 0.0) else 0.0,
                    tds = if (dto.type?.equals("Tds", true) == true) (dto.value ?: 0.0) else 0.0,
                    volume = if (dto.type?.equals("Volume", true) == true) (dto.value ?: 0.0) else 0.0,
                    createdAt = dto.date
                )
            }.sortedByDescending { it.createdAt }
            
            println("📊 Dados convertidos para relatório: ${sensorDataList.size} registros")
            Result.success(sensorDataList)
            
        } catch (e: Exception) {
            println("❌ Erro ao buscar todos os dados dos sensores: ${e.message}")
            e.printStackTrace()
            Result.failure(Exception("Erro ao buscar todos os dados dos sensores: ${e.message}"))
        }
    }

    override suspend fun updateSensorData(
        tankId: String,
        temperature: Double,
        ph: Double,
        tds: Double
    ): Result<SensorData> {
        return try {
            // Flask API não tem endpoint para update de sensores
            // Os dados são tipicamente apenas lidos dos sensores
            Result.failure(Exception("Update sensor data not supported by API"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}