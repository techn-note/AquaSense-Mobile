package com.technote.aquasense.domain.usecase.sensor

import com.technote.aquasense.data.model.SensorData
import com.technote.aquasense.domain.repository.SensorRepository
import javax.inject.Inject

/**
 * Use Case para obter dados atuais dos sensores
 *
 * Responsabilidades:
 * - Validar ID do aquário
 * - Buscar dados dos sensores
 * - Validar qualidade dos dados
 */
class GetSensorDataUseCase @Inject constructor(
    private val sensorRepository: SensorRepository
) {

    /**
     * Obtém dados atuais dos sensores
     *
     * @param tankId ID do aquário
     * @return Result com dados dos sensores ou erro
     */
    suspend operator fun invoke(tankId: String): Result<SensorData> {

        // Validação de entrada
        if (tankId.isBlank()) {
            return Result.failure(Exception("ID do aquário é obrigatório"))
        }

        return try {
            val result = sensorRepository.getSensorData(tankId)

            // Validar dados recebidos
            result.map { sensorData ->
                validateSensorData(sensorData)
                sensorData
            }
        } catch (e: Exception) {
            Result.failure(Exception("Erro ao buscar dados dos sensores: ${e.message}"))
        }
    }

    /**
     * Valida se os dados dos sensores estão em faixas aceitáveis
     */
    private fun validateSensorData(data: SensorData) {
        // Temperatura: faixa típica para aquários (15-35°C)
        if (data.temperature < 15 || data.temperature > 35) {
            // Log warning - dados fora da faixa normal
            println("⚠️ Temperatura fora da faixa normal: ${data.temperature}°C")
        }

        // pH: faixa típica (6.0-8.5)
        if (data.ph < 6.0 || data.ph > 8.5) {
            println("⚠️ pH fora da faixa normal: ${data.ph}")
        }

        // TDS: faixa típica (0-1000 ppm)
        if (data.tds < 0 || data.tds > 1000) {
            println("⚠️ TDS fora da faixa normal: ${data.tds} ppm")
        }
    }
}