package com.technote.aquasense.domain.usecase.sensor

import com.technote.aquasense.data.model.SensorData
import com.technote.aquasense.domain.repository.SensorRepository
import javax.inject.Inject

/**
 * Use Case para atualizar dados dos sensores
 *
 * Responsabilidades:
 * - Validar dados de entrada
 * - Verificar faixas aceitáveis
 * - Atualizar via repository
 */
class UpdateSensorDataUseCase @Inject constructor(
    private val sensorRepository: SensorRepository
) {

    /**
     * Atualiza dados dos sensores
     *
     * @param tankId ID do aquário
     * @param temperature Temperatura em Celsius
     * @param ph Nível de pH
     * @param tds Total de sólidos dissolvidos (ppm)
     * @return Result com dados atualizados ou erro
     */
    suspend operator fun invoke(
        tankId: String,
        temperature: Double,
        ph: Double,
        tds: Double
    ): Result<SensorData> {

        // Validação do tankId
        if (tankId.isBlank()) {
            return Result.failure(Exception("ID do aquário é obrigatório"))
        }

        // Validação da temperatura
        if (temperature < -10 || temperature > 50) {
            return Result.failure(Exception("Temperatura inválida (deve estar entre -10°C e 50°C)"))
        }

        // Validação do pH
        if (ph < 0 || ph > 14) {
            return Result.failure(Exception("pH inválido (deve estar entre 0 e 14)"))
        }

        // Validação do TDS
        if (tds < 0 || tds > 5000) {
            return Result.failure(Exception("TDS inválido (deve estar entre 0 e 5000 ppm)"))
        }

        return try {
            // Emitir warnings se os valores estão fora das faixas ideais
            checkIdealRanges(temperature, ph, tds)

            sensorRepository.updateSensorData(
                tankId = tankId,
                temperature = temperature,
                ph = ph,
                tds = tds
            )
        } catch (e: Exception) {
            Result.failure(Exception("Erro ao atualizar dados dos sensores: ${e.message}"))
        }
    }

    /**
     * Verifica se os valores estão nas faixas ideais
     */
    private fun checkIdealRanges(temperature: Double, ph: Double, tds: Double) {
        if (temperature < 22 || temperature > 28) {
            println("⚠️ Temperatura fora da faixa ideal (22-28°C): ${temperature}°C")
        }

        if (ph < 6.5 || ph > 7.5) {
            println("⚠️ pH fora da faixa ideal (6.5-7.5): $ph")
        }

        if (tds > 300) {
            println("⚠️ TDS alto (ideal < 300 ppm): $tds ppm")
        }
    }
}