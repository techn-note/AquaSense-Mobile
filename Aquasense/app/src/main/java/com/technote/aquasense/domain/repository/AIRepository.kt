package com.technote.aquasense.domain.repository

import com.technote.aquasense.data.remote.dto.NeuralPredictRequest
import com.technote.aquasense.data.remote.dto.NeuralPredictResponse

interface AIRepository {
    /**
     * Obtém predição neural network da qualidade da água
     * @param request Dados dos sensores para análise
     * @return Result com resposta da IA ou erro
     */
    suspend fun getNeuralPrediction(request: NeuralPredictRequest): Result<NeuralPredictResponse>
}