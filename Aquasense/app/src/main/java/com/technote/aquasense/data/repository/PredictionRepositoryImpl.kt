package com.technote.aquasense.data.repository

import com.technote.aquasense.data.model.Alert
import com.technote.aquasense.data.model.PredictionData
import com.technote.aquasense.data.model.WaterPrediction
import com.technote.aquasense.data.remote.api.AquasenseApi
import com.technote.aquasense.domain.repository.PredictionRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PredictionRepositoryImpl @Inject constructor(
    private val api: AquasenseApi
) : PredictionRepository {

    override suspend fun getPrediction(tankId: String): Result<WaterPrediction> {
        return try {
            val response = api.getPrediction(tankId)

            if (response.isSuccessful) {
                val dto = response.body()!!

                // Converte AlertDto para Alert (seguindo estrutura do DTO)
                val alerts = dto.prediction.alerts.map { alertDto ->
                    Alert(
                        type = alertDto.type,
                        message = alertDto.message,
                        severity = alertDto.severity
                    )
                }

                // Converte PredictionData (seguindo estrutura do DTO)
                val predictionData = PredictionData(
                    waterQuality = dto.prediction.waterQuality,
                    recommendations = dto.prediction.recommendations,
                    alerts = alerts
                )

                // Converte o DTO principal para Domain Model
                val prediction = WaterPrediction(
                    tankId = dto.tankId,
                    prediction = predictionData,
                    confidence = dto.confidence,
                    timestamp = dto.timestamp
                )

                Result.success(prediction)
            } else {
                Result.failure(Exception("Não foi possível carregar as previsões. Tente novamente."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}