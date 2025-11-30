package com.technote.aquasense.domain.usecase.sensor

import com.technote.aquasense.data.model.SensorData
import com.technote.aquasense.domain.repository.SensorRepository
import javax.inject.Inject

/**
 * Use Case para obter histórico dos sensores
 *
 * Responsabilidades:
 * - Validar ID do aquário
 * - Buscar histórico dos sensores
 * - Ordenar por data/hora
 */
class GetSensorHistoryUseCase @Inject constructor(
    private val sensorRepository: SensorRepository
) {

    /**
     * Obtém histórico dos sensores
     *
     * @param tankId ID do aquário
     * @return Result com lista histórica ou erro
     */
    suspend operator fun invoke(tankId: String): Result<List<SensorData>> {

        // Validação de entrada
        if (tankId.isBlank()) {
            return Result.failure(Exception("ID do aquário é obrigatório"))
        }

        return try {
            val result = sensorRepository.getSensorHistory(tankId)

            // Ordenar por data (mais recentes primeiro)
            result.map { historyList ->
                historyList.sortedByDescending { sensorData ->
                    sensorData.createdAt ?: ""
                }
            }
        } catch (e: Exception) {
            Result.failure(Exception("Erro ao buscar histórico dos sensores: ${e.message}"))
        }
    }
}