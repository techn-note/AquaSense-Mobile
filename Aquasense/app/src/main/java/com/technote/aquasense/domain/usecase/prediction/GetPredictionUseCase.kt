package com.technote.aquasense.domain.usecase.prediction

import com.technote.aquasense.data.model.WaterPrediction
import com.technote.aquasense.domain.repository.PredictionRepository
import javax.inject.Inject

/**
 * Use Case para obter predições da IA
 *
 * Responsabilidades:
 * - Validar ID do aquário
 * - Buscar predições via repository
 * - Processar alertas críticos
 */
class GetPredictionUseCase @Inject constructor(
    private val predictionRepository: PredictionRepository
) {

    /**
     * Obtém predições da IA para um aquário
     *
     * @param tankId ID do aquário
     * @return Result com predições ou erro
     */
    suspend operator fun invoke(tankId: String): Result<WaterPrediction> {

        // Validação de entrada
        if (tankId.isBlank()) {
            return Result.failure(Exception("ID do aquário é obrigatório"))
        }

        return try {
            val result = predictionRepository.getPrediction(tankId)

            // Processar predições para adicionar lógica adicional
            result.map { prediction ->
                processPrediction(prediction)
                prediction
            }
        } catch (e: Exception) {
            Result.failure(Exception("Erro ao obter predições: ${e.message}"))
        }
    }

    /**
     * Processa predições para lógica adicional
     */
    private fun processPrediction(prediction: WaterPrediction) {
        // Verificar confiança da predição
        if (prediction.confidence < 0.7) {
            println("⚠️ Predição com baixa confiança: ${(prediction.confidence * 100).toInt()}%")
        }

        // Verificar alertas críticos
        val criticalAlerts = prediction.prediction.alerts.filter { alert ->
            alert.severity.lowercase() == "high"
        }

        if (criticalAlerts.isNotEmpty()) {
            println("🚨 ${criticalAlerts.size} alertas críticos encontrados!")
            criticalAlerts.forEach { alert ->
                println("   - ${alert.type}: ${alert.message}")
            }
        }

        // Log da qualidade da água
        println("💧 Qualidade da água: ${prediction.prediction.waterQuality}")

        // Log das recomendações
        if (prediction.prediction.recommendations.isNotEmpty()) {
            println("📋 ${prediction.prediction.recommendations.size} recomendações disponíveis")
        }
    }
}