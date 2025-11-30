package com.technote.aquasense.domain.repository

import com.technote.aquasense.data.model.WaterPrediction


interface PredictionRepository {
    /**
     * Obtém predições da IA para um aquário
     * @param tankId ID do aquário
     * @return Result com predições ou erro
     */
    suspend fun getPrediction(tankId: String): Result<WaterPrediction>
}